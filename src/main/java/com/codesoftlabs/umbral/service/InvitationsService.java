package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.EmailTemplateType;
import com.codesoftlabs.umbral.entity.Account;
import com.codesoftlabs.umbral.entity.Invitation;
import com.codesoftlabs.umbral.entity.User;
import com.codesoftlabs.umbral.repository.AccountRepository;
import com.codesoftlabs.umbral.repository.InvitationRepository;
import com.codesoftlabs.umbral.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvitationsService {

    private static final long INVITATION_EXPIRATION_HOURS = 7 * 24;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final MailService mailService;

    public InvitationsService(InvitationRepository invitationRepository,
            UserRepository userRepository,
            AccountRepository accountRepository,
            MailService mailService) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.mailService = mailService;
    }

    @Transactional
    public Map<String, String> create(UUID userId, UUID accountId, String inviteeEmail, String role) {
        User inviter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Inviter not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Optional<User> invitee = userRepository.findByEmail(inviteeEmail);
        UUID inviteeId = invitee.map(User::getId).orElse(null);

        Invitation invitation = Invitation.builder()
                .inviterId(inviter.getId())
                .inviteeId(inviteeId)
                .inviteeEmail(inviteeEmail)
                .accountId(accountId)
                .role(role)
                .status("PENDING")
                .expiresAt(LocalDateTime.now().plusHours(INVITATION_EXPIRATION_HOURS))
                .build();

        invitationRepository.save(invitation);

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("inviterName", inviter.getFirstName());
        emailVariables.put("accountName", account.getName());
        emailVariables.put("invitationToken", invitation.getId().toString());
        emailVariables.put("role", role);

        mailService.sendEmail(inviteeEmail, inviteeEmail, EmailTemplateType.INVITATION, emailVariables);
        log.info("Invitation created and sent to {} for account {}", inviteeEmail, accountId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Invitation sent successfully");
        response.put("invitationId", invitation.getId().toString());
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findByToken(String token) {
        try {
            UUID invitationId = UUID.fromString(token);
            Invitation invitation = invitationRepository.findById(invitationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

            if ("EXPIRED".equals(invitation.getStatus()) ||
                    (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(LocalDateTime.now()))) {
                return Collections.singletonMap("error", "Invitation has expired");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", invitation.getId().toString());
            response.put("inviteeEmail", invitation.getInviteeEmail());
            response.put("accountId", invitation.getAccountId().toString());
            response.put("role", invitation.getRole());
            response.put("status", invitation.getStatus());
            response.put("expiresAt", invitation.getExpiresAt());
            return response;
        } catch (IllegalArgumentException e) {
            return Collections.singletonMap("error", "Invalid invitation token");
        }
    }

    @Transactional
    public Map<String, String> accept(UUID userId, String userEmail, String token) {
        try {
            UUID invitationId = UUID.fromString(token);
            Invitation invitation = invitationRepository.findById(invitationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

            if (!"PENDING".equals(invitation.getStatus())) {
                return Collections.singletonMap("message", "Invitation already processed");
            }

            if (invitation.getExpiresAt() != null && invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
                invitation.setStatus("EXPIRED");
                invitationRepository.save(invitation);
                return Collections.singletonMap("error", "Invitation has expired");
            }

            if (!invitation.getInviteeEmail().equalsIgnoreCase(userEmail)) {
                return Collections.singletonMap("error", "Email does not match invitation");
            }

            invitation.setInviteeId(userId);
            invitation.setStatus("ACCEPTED");
            invitation.setAcceptedAt(LocalDateTime.now());
            invitationRepository.save(invitation);

            User inviter = invitation.getInviter();
            /*
             * notificationsGateway.notifyUser(inviter.getId().toString(),
             * "invitation_accepted", Map.of(
             * "title", "Invitation Accepted",
             * "message", userEmail + " has joined your account",
             * "type", "success"
             * ));
             */

            log.info("Invitation {} accepted by {}", invitationId, userEmail);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invitation accepted successfully");
            return response;
        } catch (IllegalArgumentException e) {
            return Collections.singletonMap("error", "Invalid invitation token");
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAllSent(UUID userId) {
        return invitationRepository.findAll().stream()
                .filter(inv -> inv.getInviterId().equals(userId))
                .map(this::mapInvitationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAllReceived(UUID userId, String userEmail) {
        return invitationRepository.findAll().stream()
                .filter(inv -> inv.getInviteeEmail().equalsIgnoreCase(userEmail) ||
                        (inv.getInviteeId() != null && inv.getInviteeId().equals(userId)))
                .map(this::mapInvitationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void remove(UUID userId, UUID invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        if (!invitation.getInviterId().equals(userId)) {
            throw new IllegalArgumentException("Only inviter can delete the invitation");
        }

        invitationRepository.deleteById(invitationId);
        log.info("Invitation {} deleted by user {}", invitationId, userId);
    }

    private Map<String, Object> mapInvitationToResponse(Invitation invitation) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", invitation.getId().toString());
        response.put("inviterId", invitation.getInviterId().toString());
        response.put("inviteeEmail", invitation.getInviteeEmail());
        response.put("accountId", invitation.getAccountId().toString());
        response.put("role", invitation.getRole());
        response.put("status", invitation.getStatus());
        response.put("expiresAt", invitation.getExpiresAt());
        response.put("acceptedAt", invitation.getAcceptedAt());
        return response;
    }
}
