package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.CreateUserDto;
import com.codesoftlabs.umbral.dto.UpdateUserDto;
import com.codesoftlabs.umbral.entity.User;
import com.codesoftlabs.umbral.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(CreateUserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    @Cacheable(value = "usersById", key = "#id")
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Cacheable(value = "usersByEmail", key = "#email")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Optional<User> findByEmailWithPassword(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByIdWithPassword(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersById", key = "#id"),
            @CacheEvict(value = "usersByEmail", allEntries = true)
    })
    public User update(UUID id, UpdateUserDto updateDto) {
        User user = findById(id);

        if (updateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        if (updateDto.getMfaSecret() != null) {
            user.setMfaSecret(updateDto.getMfaSecret());
        }

        return userRepository.save(user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersById", key = "#id"),
            @CacheEvict(value = "usersByEmail", allEntries = true)
    })
    public User markAsActive(UUID id) {
        User user = findById(id);
        user.setIsActive(true);
        return userRepository.save(user);
    }
}