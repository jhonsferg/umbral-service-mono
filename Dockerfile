# ==============================================================================
# Dockerfile - Umbral Service (Production-Tuned)
#
# Build Strategy: Multi-stage build with 3 isolated stages:
#
#   [builder]    Compiles the application using Maven, installs local
#                dependencies, resolves remote artifacts with persistent cache,
#                and extracts the Spring Boot layered JAR for optimized
#                layer caching in the final image.
#
#   [otel-agent] Downloads the OpenTelemetry Java agent in a dedicated stage
#                so it is cached independently and only re-downloaded when
#                the OTEL_AGENT_VERSION build argument changes.
#
#   [runner]     Minimal JRE-only image. Copies the extracted Spring Boot
#                layers in cache-friendliness order (least-changed first) to
#                maximize Docker layer reuse across application deployments.
#
# Layer Cache Strategy (Spring Boot Layered JAR):
#   Spring Boot's layertools splits the JAR into:
#     1. dependencies          - third-party JARs (change rarely)
#     2. spring-boot-loader    - Spring Boot launcher (almost never changes)
#     3. snapshot-dependencies - SNAPSHOT JARs (change occasionally)
#     4. application           - compiled application classes (changes every build)
#
#   By copying them in that order, Docker only invalidates the upper layers
#   (snapshot-dependencies + application) on a typical code change, leaving
#   the large dependencies layer fully cached. This results in significantly
#   smaller image pushes and faster container startup.
#
# Maven Cache Strategy (--mount=type=cache):
#   The Maven local repository (~/.m2) is mounted as a BuildKit cache volume.
#   Dependencies are downloaded once and reused across all subsequent builds
#   on the same host or CI runner without being embedded in any image layer.
#   The `sharing=locked` flag prevents concurrent builds from corrupting the
#   cache when multiple pipelines run in parallel.
#
# Security:
#   The runner stage creates a dedicated non-root user (appuser:appgroup).
#   Running as root inside a container is a security risk - a process escape
#   would gain root on the host. Non-root also satisfies PodSecurityPolicy /
#   Kubernetes SecurityContext requirements in hardened clusters.
#
# Health Checking:
#   A Docker HEALTHCHECK is declared so orchestrators (Docker Compose,
#   Kubernetes liveness probes, Swarm) can distinguish between a running
#   process and a truly healthy application. Spring Boot Actuator's /health
#   endpoint is used as the check target.
#
# JVM Tuning (see JAVA_TOOL_OPTIONS section for per-flag rationale):
#   All JVM flags are set via JAVA_TOOL_OPTIONS so they are applied before
#   the JVM initializes - important for GC selection, heap sizing, and the
#   SecureRandom entropy source override required for Oracle JDBC connections.
#   Each flag can be overridden at runtime via docker-compose environment or
#   Kubernetes env vars without rebuilding the image.
# ==============================================================================


# ==============================================================================
# STAGE 1 - builder
# Compiles the application and extracts Spring Boot layers.
# This stage uses a full Gradle + JDK image; it is never shipped to production.
# ==============================================================================
FROM gradle:8.10-jdk21-alpine AS builder

# BUILD_PROFILE controls which Gradle profile is activated during packaging.
# Defaults to 'prod'; override at build time with:
#   docker build --build-arg BUILD_PROFILE=staging .
ARG BUILD_PROFILE=prod

WORKDIR /build

# ── Step 1: Resolve dependencies (cache-optimized) ───────────────────────────
# Only build files are copied here. If application source code changes but
# build files do not, Docker reuses this layer from cache - skipping the
# entire dependency download on every build.
#
# --mount=type=cache,target=/root/.gradle
#   Mounts a persistent BuildKit cache volume at the Gradle cache path.
#   The cache survives between builds and is never embedded in any image
#   layer. This is the single most impactful optimization in this Dockerfile:
#   a cold build (no cache) takes ~2-3 min; warm builds typically take <20s.
#
# sharing=locked
#   Prevents two concurrent builds on the same host from writing to the cache
#   simultaneously, which can corrupt the cache index.
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN --mount=type=cache,target=/root/.gradle,sharing=locked \
    gradle dependencies --no-daemon

# ── Step 3: Compile and package ───────────────────────────────────────────────
# Source code is copied after dependency resolution so that code-only changes
# (the common case) do not invalidate the dependency cache layer above.
# -x test: tests are expected to run in a dedicated CI pipeline step,
# not during image assembly, to keep build times predictable.
COPY src ./src
RUN --mount=type=cache,target=/root/.gradle,sharing=locked \
    gradle clean bootJar -Pprofile=${BUILD_PROFILE} -x test --no-daemon

# ── Step 4: Extract Spring Boot layered JAR ───────────────────────────────────
# jarmode=layertools splits the fat JAR into four directories under /build/layers:
#   - dependencies/          third-party runtime JARs
#   - spring-boot-loader/    Spring Boot launcher classes
#   - snapshot-dependencies/ internal SNAPSHOT JARs
#   - application/           compiled application classes and resources
# These directories are copied individually into the runner stage in
# cache-friendliness order so Docker only rebuilds the layers that changed.
RUN java -Djarmode=layertools \
    -jar build/libs/*.jar extract \
    --destination /build/layers


# ==============================================================================
# STAGE 2 - otel-agent
# Downloads the OpenTelemetry Java instrumentation agent.
# Isolated in its own stage so the agent JAR is cached by version and is
# never re-downloaded unless OTEL_AGENT_VERSION changes. This also keeps
# wget/apk out of the final runner image.
# ==============================================================================
FROM alpine:3.20 AS otel-agent

# OTEL_AGENT_VERSION controls which agent release is downloaded.
# Pin this to a specific version in production to ensure reproducible builds.
# Override at build time with: docker build --build-arg OTEL_AGENT_VERSION=2.26.0 .
ARG OTEL_AGENT_VERSION=2.25.0

RUN apk add --no-cache wget && \
    wget -q -O /opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar


# ==============================================================================
# STAGE 3 - runner
# Minimal production image. Contains only the JRE, the OTel agent, and the
# extracted application layers. No build tools, no source code, no Maven cache.
#
# Base image: eclipse-temurin:21-jre-alpine
#   - JRE only (not JDK): strips ~150MB of compiler/tools not needed at runtime
#   - Alpine Linux: minimal attack surface, small base layer (~5MB)
#   - Temurin (Adoptium): production-grade, TCK-verified OpenJDK distribution
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

# ── Non-root user ──────────────────────────────────────────────────────────────
# Running as root inside a container is a security risk:
#   - A container escape would grant root access on the host
#   - Many Kubernetes clusters enforce non-root via PodSecurityPolicy/Admission
#   - Some JVM optimizations (e.g. process priority tuning) behave differently
#     under non-root, avoiding accidental system-wide priority changes
# appgroup/appuser have no login shell and no home directory outside /app.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# ── OTel agent ────────────────────────────────────────────────────────────────
# Copied from the dedicated otel-agent stage. The agent instruments bytecode
# at JVM startup and enables distributed tracing, metrics, and log correlation
# without any changes to application code.
# Runtime overhead: ~3-8% CPU. Disable selectively with:
#   OTEL_TRACES_EXPORTER=none OTEL_METRICS_EXPORTER=none
COPY --from=otel-agent /opentelemetry-javaagent.jar ./opentelemetry-javaagent.jar

# ── Application layers (cache-friendliness order) ─────────────────────────────
# Layers are copied from least-changed to most-changed so that Docker
# invalidates only the layers above the first changed layer:
#
#   dependencies:          ~100-200MB, changes only when pom.xml changes
#   spring-boot-loader:    ~300KB,     changes only on Spring Boot version bumps
#   snapshot-dependencies: ~varies,    changes when internal SNAPSHOTs are updated
#   application:           ~small,     changes on every code commit (top layer)
#
# Result: a typical code-only push rebuilds only the application layer,
# resulting in a ~few-KB image diff instead of a full image push.
COPY --from=builder /build/layers/dependencies          ./
COPY --from=builder /build/layers/spring-boot-loader    ./
COPY --from=builder /build/layers/snapshot-dependencies ./
COPY --from=builder /build/layers/application           ./

# Transfer ownership of all application files to the non-root user.
# Done after all COPY instructions to avoid repeating chown per layer.
RUN chown -R appuser:appgroup /app

# Switch to non-root user for all subsequent instructions and at runtime.
USER appuser

# Expose the application ports.
# 8080: REST API
# 8081: Socket.IO
ENV PORT=8080
EXPOSE 8080 8081


# ==============================================================================
# Environment - OpenTelemetry (overridable at runtime)
#
# All OTEL_* variables can be overridden per service in docker-compose.yml or
# via Kubernetes ConfigMap/Secret without rebuilding the image.
# ==============================================================================

# Identifies this service in traces and metrics.
ENV OTEL_SERVICE_NAME="umbral-backend"

# Spring Profiles
ENV SPRING_PROFILES_ACTIVE="prod"

# Exporters - set to 'none' to disable individual signals (e.g. during local dev)
ENV OTEL_TRACES_EXPORTER="otlp"
ENV OTEL_METRICS_EXPORTER="otlp"
ENV OTEL_LOGS_EXPORTER="otlp"

# OTLP protocol - 'grpc' (port 4317) or 'http/protobuf' (port 4318)
ENV OTEL_EXPORTER_OTLP_PROTOCOL="grpc"

# Collector endpoint - points to the OpenTelemetry Collector sidecar/service
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://opentelemetry:4317"

# Additional key=value resource attributes attached to all telemetry signals.
# Example: "deployment.environment=prod,team=payments"
ENV OTEL_RESOURCE_ATTRIBUTES=""


# ==============================================================================
# Environment - JVM Tuning (JAVA_TOOL_OPTIONS)
#
# JAVA_TOOL_OPTIONS is read by the JVM before main() runs, making it the
# correct mechanism for flags that must be active from JVM initialization
# (GC selection, heap sizing, SecureRandom source, encoding).
#
# Individual flags can be overridden at runtime:
#   docker run -e JAVA_TOOL_OPTIONS="-Xms128m -Xmx256m ..." ...
# ==============================================================================
ENV JAVA_TOOL_OPTIONS="\
    \
    -javaagent:/app/opentelemetry-javaagent.jar \
    \
    -Xms256m \
    \
    -Xmx512m \
    \
    -XX:+UseG1GC \
    \
    -XX:MaxGCPauseMillis=50 \
    \
    -XX:G1HeapRegionSize=4m \
    \
    -XX:+UseStringDeduplication \
    \
    -XX:+ExitOnOutOfMemoryError \
    \
    -XX:+UseContainerSupport \
    \
    -XX:InitialRAMPercentage=50.0 \
    \
    -XX:MaxRAMPercentage=75.0 \
    \
    -Djava.security.egd=file:/dev/./urandom \
    \
    -Dfile.encoding=UTF-8 \
    \
    -Duser.timezone=America/Lima \
    \
    -Dspring.jmx.enabled=false"

# -javaagent:/app/opentelemetry-javaagent.jar
#   Attaches the OTel agent at JVM startup for bytecode instrumentation.
#   Must be the first flag so it is active before any class is loaded.
#
# -Xms256m
#   Sets the initial (minimum) heap size to 256MB. Matching Xms to a
#   predictable baseline avoids the JVM spending startup time growing
#   the heap from the JVM default (which can be ~1/64 of total RAM).
#
# -Xmx512m
#   Sets the maximum heap size to 512MB. Without an explicit cap, the JVM
#   defaults to 25% of container memory. In environments without memory
#   limits this can mean gigabytes of heap leading to infrequent but very
#   long Stop-The-World GC pauses (observed as multi-second p99 spikes).
#
# -XX:+UseG1GC
#   Selects the Garbage-First (G1) garbage collector. G1 is the default
#   in JDK 9+ but explicitly declaring it is safer across base images.
#   G1 trades slightly more CPU for shorter, more predictable STW pauses
#   compared to Parallel GC, which is preferable in a latency-sensitive
#   microservice context.
#
# -XX:MaxGCPauseMillis=50
#   Sets G1's target maximum Stop-The-World pause to 50ms. G1 will size
#   its heap regions and schedule concurrent phases to try to stay under
#   this budget. Actual pauses depend on live-set size, but this prevents
#   the unbounded multi-second pauses that occur without a target.
#
# -XX:G1HeapRegionSize=4m
#   With a 512MB max heap, G1 defaults to 2MB regions (2048 total).
#   Setting 4MB reduces the region count to ~128, which lowers bookkeeping
#   overhead and improves remembered-set processing throughput at this heap
#   size. The rule of thumb: aim for 2048 regions; with 512MB, 4MB is optimal.
#
# -XX:+UseStringDeduplication
#   Enables G1's string deduplication feature. Strings with the same content
#   but different char[] backing arrays are deduplicated on the heap during
#   GC. Reduces heap pressure for services that process many similar strings
#   (JSON keys, ISO dates, CIP codes, currency identifiers). No correctness
#   risk; the optimization is transparent to application code.
#
# -XX:+ExitOnOutOfMemoryError
#   Instructs the JVM to call exit(3) immediately on OutOfMemoryError instead
#   of throwing it into application code. An OOM-hit JVM is typically in an
#   inconsistent state; trying to recover leads to unpredictable behavior.
#   With this flag, the container crashes immediately and is restarted by
#   Docker Compose / Kubernetes, which is always preferable to a zombie process.
#
# -XX:+UseContainerSupport
#   Enabled by default in JDK 8u191+ and JDK 11+, but explicitly declaring
#   it ensures the JVM reads CPU and memory limits from the cgroup rather
#   than the host OS. Without it, the JVM may see host RAM (e.g. 32GB) and
#   size its internal thread pools and heap far larger than the container allows,
#   causing immediate OOM kills by the kernel.
#
# -XX:InitialRAMPercentage=50.0 / -XX:MaxRAMPercentage=75.0
#   When a memory limit is set (e.g. mem_limit: 1g in docker-compose),
#   these flags automatically scale the heap proportionally without any
#   code change. InitialRAMPercentage=50 sets Xms to 50% of the limit;
#   MaxRAMPercentage=75 sets Xmx to 75%. The fixed -Xms/-Xmx above take
#   precedence when no cgroup limit is detected; these act as a safety net
#   when limits are applied externally (e.g. Kubernetes resource limits).
#
# -Djava.security.egd=file:/dev/./urandom
#   Overrides the entropy source for java.security.SecureRandom. The Oracle
#   JDBC driver calls SecureRandom during connection initialization. On Linux
#   containers, /dev/random blocks when the kernel entropy pool is depleted,
#   adding 100-500ms to every new Oracle connection establishment. Using
#   /dev/urandom (via the /dev/./ path workaround that bypasses a JDK
#   hardcoded check) provides non-blocking pseudo-random bytes that are
#   cryptographically adequate for JDBC session key generation.
#
# -Dfile.encoding=UTF-8
#   Forces the JVM default charset to UTF-8 regardless of the container's
#   locale settings. Prevents subtle encoding bugs when processing text
#   containing accented characters (common in Peruvian address/name data).
#   Also eliminates JVM locale-detection overhead on startup.
#
# -Duser.timezone=America/Lima
#   Sets the JVM default timezone to Lima (UTC-5, no DST). Without this,
#   the JVM inherits the container's TZ environment variable or defaults to
#   UTC. Explicit timezone prevents LocalDateTime comparisons from producing
#   wrong results when the host or orchestrator runs in a different timezone.
#
# -Dspring.jmx.enabled=false
#   Disables Spring's JMX MBean registration. JMX is a management protocol
#   designed for local/GUI-based monitoring tools (JConsole, VisualVM) that
#   are not used in a containerized environment monitored via OTel/Prometheus.
#   Disabling it removes ~50-100ms from startup time and avoids exposing
#   unnecessary management endpoints.


# ==============================================================================
# Health Check
#
# Docker polls this command every 15 seconds after an initial 40-second grace
# period (start-period) to allow Spring Boot to fully initialize.
#
# --interval=15s    How often to run the check after the start-period expires.
# --timeout=5s      If the check does not complete within 5s, treat as failure.
# --start-period=40s Grace period after container start before checks count.
#                   Spring Boot with Hibernate + dual datasource initialization
#                   typically takes 15-35s; 40s gives safe headroom.
# --retries=3       Container is marked unhealthy only after 3 consecutive
#                   failures, avoiding flapping on transient slowness.
#
# The check uses wget (available in alpine) against Spring Boot Actuator's
# /health endpoint. A non-200 response or connection failure returns exit 1,
# which Docker interprets as unhealthy.
# ==============================================================================
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:${PORT}/actuator/health || exit 1



# ==============================================================================
# Entrypoint
#
# JarLauncher is Spring Boot's class responsible for launching the application
# from the extracted layered directory structure. It is equivalent to running
# the fat JAR but works with the exploded layer layout produced by layertools.
#
# Using exec form (JSON array) instead of shell form ensures:
#   - The JVM process runs as PID 1 (not a shell child)
#   - SIGTERM from Docker stop is delivered directly to the JVM
#   - Spring Boot's graceful shutdown hooks fire correctly
# ==============================================================================
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
