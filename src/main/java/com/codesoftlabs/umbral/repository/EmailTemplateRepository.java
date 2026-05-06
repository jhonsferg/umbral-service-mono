package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.common.enums.EmailTemplateType;
import com.codesoftlabs.umbral.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {
    Optional<EmailTemplate> findByTypeAndIsActiveTrue(EmailTemplateType type);

    Optional<EmailTemplate> findByTemplateKeyAndIsActiveTrue(String templateKey);
}
