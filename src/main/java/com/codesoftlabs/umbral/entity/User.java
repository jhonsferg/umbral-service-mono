package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column
    private String avatarUrl;

    @Column(length = 20)
    private String phoneNumber;

    @Builder.Default
    @Column(length = 5, nullable = false)
    private String locale = "es";

    @Builder.Default
    @Column(length = 50, nullable = false)
    private String timezone = "America/Lima";

    @Builder.Default
    @Column(length = 3, nullable = false)
    private String defaultCurrency = "PEN";

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean mfaEnabled = false;

    @Column
    private String mfaSecret;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private LocalDateTime emailVerifiedAt;

    @SoftDelete
    @Column
    private LocalDateTime deletedAt;
}
