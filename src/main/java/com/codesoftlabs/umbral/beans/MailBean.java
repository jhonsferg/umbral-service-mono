package com.codesoftlabs.umbral.beans;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailBean {
    private String smtpWebUrl;
    private String emailConfirmationExpiration;
}
