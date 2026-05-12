package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.InvitationDto;
import com.codesoftlabs.umbral.entity.Invitation;
import org.springframework.stereotype.Component;

@Component
public class InvitationMapper implements EntityMapper<Invitation, InvitationDto> {

    private final UserMapper userMapper;
    private final AccountMapper accountMapper;

    public InvitationMapper(UserMapper userMapper, AccountMapper accountMapper) {
        this.userMapper = userMapper;
        this.accountMapper = accountMapper;
    }

    @Override
    public InvitationDto toDto(Invitation entity) {
        if (entity == null) {
            return null;
        }

        return InvitationDto.builder()
                .id(entity.getId())
                .inviterId(entity.getInviterId())
                .inviter(userMapper.toDto(entity.getInviter()))
                .inviteeId(entity.getInviteeId())
                .invitee(userMapper.toDto(entity.getInvitee()))
                .inviteeEmail(entity.getInviteeEmail())
                .accountId(entity.getAccountId())
                .account(accountMapper.toDto(entity.getAccount()))
                .role(entity.getRole())
                .status(entity.getStatus())
                .expiresAt(entity.getExpiresAt())
                .acceptedAt(entity.getAcceptedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Invitation toEntity(InvitationDto dto) {
        if (dto == null) {
            return null;
        }

        return Invitation.builder()
                .id(dto.getId())
                .inviterId(dto.getInviterId())
                .inviteeId(dto.getInviteeId())
                .inviteeEmail(dto.getInviteeEmail())
                .accountId(dto.getAccountId())
                .role(dto.getRole())
                .status(dto.getStatus())
                .expiresAt(dto.getExpiresAt())
                .acceptedAt(dto.getAcceptedAt())
                .build();
    }
}
