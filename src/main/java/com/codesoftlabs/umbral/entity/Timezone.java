package com.codesoftlabs.umbral.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_timezones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timezone extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "value", length = 32, nullable = false)
    private String value;

    @Column(name = "utc", length = 16, nullable = false)
    private String utc;

    @SoftDelete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
