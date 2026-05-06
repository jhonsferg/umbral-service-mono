package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "inviter_id", nullable = false)
    private UUID inviterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", insertable = false, updatable = false)
    private User inviter;

    @Column(name = "invitee_id")
    private UUID inviteeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", insertable = false, updatable = false)
    private User invitee;

    @Column(nullable = false, length = 255)
    private String inviteeEmail;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @Column(nullable = false)
    private String role; // VIEWER, EDITOR, ADMIN

    @Column(nullable = false)
    private String status; // PENDING, ACCEPTED, REJECTED, EXPIRED

    @Column
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime acceptedAt;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
