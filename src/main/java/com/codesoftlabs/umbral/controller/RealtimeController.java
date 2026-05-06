package com.codesoftlabs.umbral.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/realtime")
@Tag(name = "realtime", description = "Real-time notifications and WebSocket")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class RealtimeController {

    /**
     * WebSocket endpoint for socket.io polling and connection
     * This is a placeholder endpoint that returns a valid socket.io response
     */
    @GetMapping("/socket.io/**")
    @Operation(summary = "Socket.IO WebSocket endpoint", description = "Real-time communication endpoint using Socket.IO protocol")
    public ResponseEntity<Map<String, Object>> socketIO(
            @RequestAttribute("userId") UUID userId,
            @RequestParam(required = false) String EIO,
            @RequestParam(required = false) String transport,
            @RequestParam(required = false) String t) {

        log.debug("Socket.IO connection request - EIO: {}, transport: {}, from user: {}", EIO, transport, userId);

        // Return a valid socket.io response for polling transport
        if ("polling".equals(transport)) {
            Map<String, Object> response = new HashMap<>();
            response.put("sid", userId.toString());
            response.put("upgrades", new String[]{"websocket"});
            response.put("pingInterval", 25000);
            response.put("pingTimeout", 20000);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(new HashMap<>());
    }
}
