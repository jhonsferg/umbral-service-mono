package com.codesoftlabs.umbral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvitationDto {
    private UUID id;
    private UUID inviterId;
    private UserDto inviter;
    private UUID inviteeId;
    private UserDto invitee;
    private String inviteeEmail;
    private UUID accountId;
    private AccountDto account;
    private String role;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
