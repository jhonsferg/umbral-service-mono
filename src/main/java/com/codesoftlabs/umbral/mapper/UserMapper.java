package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.UserDto;
import com.codesoftlabs.umbral.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements EntityMapper<User, UserDto> {

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .avatarUrl(entity.getAvatarUrl())
                .phoneNumber(entity.getPhoneNumber())
                .locale(entity.getLocale())
                .timezone(entity.getTimezone())
                .defaultCurrency(entity.getDefaultCurrency())
                .isActive(entity.getIsActive())
                .mfaEnabled(entity.getMfaEnabled())
                .lastLogin(entity.getLastLogin())
                .emailVerifiedAt(entity.getEmailVerifiedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .avatarUrl(dto.getAvatarUrl())
                .phoneNumber(dto.getPhoneNumber())
                .locale(dto.getLocale())
                .timezone(dto.getTimezone())
                .defaultCurrency(dto.getDefaultCurrency())
                .isActive(dto.getIsActive())
                .mfaEnabled(dto.getMfaEnabled())
                .lastLogin(dto.getLastLogin())
                .emailVerifiedAt(dto.getEmailVerifiedAt())
                .build();
    }
}
