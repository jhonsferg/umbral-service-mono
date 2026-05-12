package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_countries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "timezone_id", nullable = false)
    private UUID timezoneId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "timezone_id", insertable = false, updatable = false)
    private Timezone timezone;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "code", length = 8, nullable = false)
    private String code;

    @Column(name = "phone", length = 8, nullable = false)
    private String phone;

    @SoftDelete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
