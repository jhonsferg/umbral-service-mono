package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_banks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "shortcut", length = 32, nullable = false)
    private String shortcut;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Column(name = "country_id", nullable = false)
    private UUID countryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    private Country country;

    @SoftDelete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
