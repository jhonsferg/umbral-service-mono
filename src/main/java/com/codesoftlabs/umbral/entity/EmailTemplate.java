package com.codesoftlabs.umbral.entity;

import com.codesoftlabs.umbral.common.enums.EmailTemplateType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_email_templates", indexes = {
        @Index(columnList = "type", unique = true),
        @Index(columnList = "template_key", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100, unique = true, nullable = false)
    private String templateKey;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private EmailTemplateType type;

    @Column(length = 200, nullable = false)
    private String subject;

    @Column(columnDefinition = "BYTEA", nullable = false)
    private byte[] htmlBodyCompressed;

    @Column(columnDefinition = "BYTEA")
    private byte[] textBodyCompressed;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
