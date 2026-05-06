package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.EmailTemplateType;
import com.codesoftlabs.umbral.entity.EmailTemplate;
import com.codesoftlabs.umbral.repository.EmailTemplateRepository;
import com.codesoftlabs.umbral.util.CompressionUtil;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final EmailTemplateRepository emailTemplateRepository;

    @Value("${spring.mail.username:noreply@umbral.app}")
    private String fromEmail;

    public MailService(JavaMailSender mailSender, EmailTemplateRepository emailTemplateRepository) {
        this.mailSender = mailSender;
        this.emailTemplateRepository = emailTemplateRepository;
    }

    public void sendEmail(String toEmail, String toName, EmailTemplateType type, Map<String, Object> variables) {
        try {
            EmailTemplate template = getTemplate(type);
            String htmlBody = CompressionUtil.decompress(template.getHtmlBodyCompressed());
            String textBody = template.getTextBodyCompressed() != null ?
                    CompressionUtil.decompress(template.getTextBodyCompressed()) : null;

            variables.put("year", java.time.Year.now().getValue());

            String subject = render(template.getSubject(), variables);
            htmlBody = render(htmlBody, variables);
            textBody = textBody != null ? render(textBody, variables) : null;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String senderEmail = fromEmail != null && !fromEmail.isEmpty() ? fromEmail : "noreply@umbral.app";
            helper.setFrom(senderEmail);
            helper.setTo(toName != null && !toName.isEmpty() ? String.format("%s <%s>", toName, toEmail) : toEmail);
            helper.setSubject(subject);

            if (textBody != null && htmlBody != null) {
                helper.setText(textBody, htmlBody);
            } else if (htmlBody != null) {
                helper.setText(htmlBody, true);
            }

            mailSender.send(message);
            logger.info("Email [{}] sent to {}", type, toEmail);
        } catch (RuntimeException e) {
            logger.warn("Template not found [{}] for {}: {}", type, toEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Error sending email [{}] to {}: {}", type, toEmail, e.getMessage());
        }
    }

    @Cacheable(value = "emailTemplates", key = "#type.name()")
    public EmailTemplate getTemplate(EmailTemplateType type) {
        return emailTemplateRepository.findByTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new RuntimeException("Email template not found for type: " + type));
    }

    @CacheEvict(value = "emailTemplates", key = "#type.name()")
    public void invalidateTemplateCache(EmailTemplateType type) {
    }

    private String render(String template, Map<String, Object> variables) {
        if (template == null) return null;
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            result = result.replaceAll("\\{\\{" + entry.getKey() + "\\}\\}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}


