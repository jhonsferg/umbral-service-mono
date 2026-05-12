package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.DeviceDto;
import com.codesoftlabs.umbral.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper implements EntityMapper<Device, DeviceDto> {

    @Override
    public DeviceDto toDto(Device entity) {
        if (entity == null) {
            return null;
        }

        return DeviceDto.builder()
                .id(entity.getId())
                .deviceName(entity.getDeviceName())
                .deviceType(entity.getDeviceType())
                .operatingSystem(entity.getOperatingSystem())
                .browser(entity.getBrowser())
                .userAgent(entity.getUserAgent())
                .ipAddress(entity.getIpAddress())
                .country(entity.getCountry())
                .city(entity.getCity())
                .lastActivityAt(entity.getLastActivityAt())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Device toEntity(DeviceDto dto) {
        if (dto == null) {
            return null;
        }

        return Device.builder()
                .id(dto.getId())
                .deviceName(dto.getDeviceName())
                .deviceType(dto.getDeviceType())
                .operatingSystem(dto.getOperatingSystem())
                .browser(dto.getBrowser())
                .userAgent(dto.getUserAgent())
                .ipAddress(dto.getIpAddress())
                .country(dto.getCountry())
                .city(dto.getCity())
                .lastActivityAt(dto.getLastActivityAt())
                .isActive(dto.getIsActive())
                .build();
    }
}
