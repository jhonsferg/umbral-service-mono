package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findByUserIdOrderByLastActivityAtDesc(UUID userId);

    Optional<Device> findByUserIdAndUserAgent(UUID userId, String userAgent);

    List<Device> findByUserIdAndIsActiveTrue(UUID userId);
}
