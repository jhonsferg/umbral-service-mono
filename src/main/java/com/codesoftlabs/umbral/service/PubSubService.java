package com.codesoftlabs.umbral.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubSubService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes a message to a Redis channel.
     *
     * @param channel The channel name.
     * @param message The message object to publish.
     */
    public void publish(String channel, Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(channel, jsonMessage);
            log.debug("Published message to channel {}: {}", channel, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message for channel {}: {}", channel, message, e);
        }
    }
}
