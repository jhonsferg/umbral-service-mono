package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.EmailTemplateType;
import com.codesoftlabs.umbral.entity.EmailTemplate;
import com.codesoftlabs.umbral.repository.EmailTemplateRepository;
import com.codesoftlabs.umbral.util.CompressionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailTemplateInitializer {

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateInitializer.class);
    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplateInitializer(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeTemplates() {
        try {
            initializeConfirmEmailTemplate();
            initializeResetPasswordTemplate();
            initializeInvitationTemplate();
            initializeWelcomeTemplate();
            initializeBudgetAlertTemplate();
            logger.info("Email templates initialized successfully");
        } catch (org.hibernate.HibernateException e) {
            logger.warn("Email template table not yet available, skipping initialization: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error initializing email templates", e);
        }
    }

    private void initializeConfirmEmailTemplate() throws Exception {
        if (emailTemplateRepository.findByTemplateKeyAndIsActiveTrue("confirm_email").isPresent()) {
            return;
        }

        String htmlBody = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <meta name="x-apple-disable-message-reformatting" />
                    <meta name="format-detection" content="telephone=no,address=no,email=no,date=no,url=no" />
                    <title>Confirma tu correo — Umbral</title>
                    <style>
                      #outlook a { padding: 0; }
                      body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                      table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                      img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
                      @media only screen and (max-width: 620px) {
                        .wrapper { width: 100% !important; padding: 0 !important; }
                        .container { width: 100% !important; border-radius: 0 !important; }
                        .content-pad { padding: 32px 24px !important; }
                        .header-pad { padding: 32px 24px !important; }
                        .btn-table { width: 100% !important; }
                        .btn-link { display: block !important; text-align: center !important; }
                        .footer-pad { padding: 20px 24px !important; }
                      }
                    </style>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #0f0f13; font-family: Georgia, 'Times New Roman', serif">
                    <div style="display: none; font-size: 1px; color: #0f0f13; line-height: 1px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden">
                      Hola {{firstName}}, confirma tu correo para activar tu cuenta en Umbral.
                    </div>
                    <table class="wrapper" role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; padding: 48px 16px">
                      <tr>
                        <td align="center">
                          <table class="container" role="presentation" width="580" cellpadding="0" cellspacing="0" style="background-color: #16161d; border-radius: 16px; overflow: hidden; border: 1px solid #2a2a35">
                            <tr>
                              <td class="header-pad" style="padding: 48px 48px 40px; text-align: center; background-color: #16161d; border-bottom: 1px solid #2a2a35">
                                <table role="presentation" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto 20px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 12px; width: 52px; height: 52px; text-align: center; vertical-align: middle;">
                                      <span style="font-size: 24px; line-height: 52px; display: block">◈</span>
                                    </td>
                                  </tr>
                                </table>
                                <h1 style="margin: 0; font-family: Georgia, 'Times New Roman', serif; font-size: 26px; font-weight: 400; color: #e8c98a; letter-spacing: 6px; text-transform: uppercase;">UMBRAL</h1>
                                <p style="margin: 6px 0 0; font-family: Arial, sans-serif; font-size: 11px; color: #5a5a6e; letter-spacing: 3px; text-transform: uppercase">Finanzas Personales</p>
                              </td>
                            </tr>
                            <tr>
                              <td class="content-pad" style="padding: 48px">
                                <h2 style="margin: 0 0 16px; font-family: Georgia, 'Times New Roman', serif; font-size: 28px; font-weight: 400; color: #f0ece4; line-height: 1.3">
                                  Bienvenido,<br /><span style="color: #c9a96e">{{firstName}}</span>
                                </h2>
                                <p style="margin: 0 0 12px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Tu cuenta en Umbral ha sido creada exitosamente. Para comenzar a ordenar tus finanzas, necesitamos verificar tu dirección de correo electrónico.
                                </p>
                                <p style="margin: 0 0 36px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Este paso es importante para proteger tu información financiera.
                                </p>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 36px">
                                  <tr>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                    <td style="padding: 0 16px; white-space: nowrap; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Confirmar identidad</td>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                  </tr>
                                </table>
                                <table class="btn-table" role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 0 36px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 8px">
                                      <a class="btn-link" href="{{confirmUrl}}" style="display: inline-block; padding: 16px 40px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #0f0f13; text-decoration: none; letter-spacing: 2px; text-transform: uppercase;">Confirmar mi correo</a>
                                    </td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; border-radius: 8px; border: 1px solid #2a2a35; margin-bottom: 36px">
                                  <tr>
                                    <td style="padding: 16px 20px">
                                      <p style="margin: 0 0 6px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Si el botón no funciona, usa este enlace:</p>
                                      <a href="{{confirmUrl}}" style="font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #c9a96e; text-decoration: none; word-break: break-all">{{confirmUrl}}</a>
                                    </td>
                                  </tr>
                                </table>
                                <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 13px; color: #4a4a5e; line-height: 1.6; border-left: 3px solid #2a2a35; padding-left: 16px;">
                                  Este enlace expira en <strong style="color: #6a6a7e">30 días</strong>. Si no creaste esta cuenta, puedes ignorar este correo con total seguridad.
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td class="footer-pad" style="padding: 24px 48px; background-color: #0f0f13; border-top: 1px solid #1e1e28; text-align: center">
                                <p style="margin: 0 0 8px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">© {{year}} Umbral — Todos los derechos reservados</p>
                                <p style="margin: 0; font-family: Arial, sans-serif; font-size: 11px; color: #2a2a35">Este correo fue enviado a <span style="color: #4a4a5e">{{email}}</span></p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """;

        EmailTemplate template = EmailTemplate.builder()
                .templateKey("confirm_email")
                .type(EmailTemplateType.CONFIRM_EMAIL)
                .subject("Confirma tu correo — Umbral")
                .htmlBodyCompressed(CompressionUtil.compress(htmlBody))
                .isActive(true)
                .build();

        emailTemplateRepository.save(template);
        logger.info("CONFIRM_EMAIL template initialized with template_key: confirm_email");
    }

    private void initializeResetPasswordTemplate() throws Exception {
        if (emailTemplateRepository.findByTemplateKeyAndIsActiveTrue("reset_password").isPresent()) {
            return;
        }

        String htmlBody = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <meta name="x-apple-disable-message-reformatting" />
                    <meta name="format-detection" content="telephone=no,address=no,email=no,date=no,url=no" />
                    <title>Recupera tu contraseña — Umbral</title>
                    <style>
                      #outlook a { padding: 0; }
                      body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                      table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                      img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
                      @media only screen and (max-width: 620px) {
                        .wrapper { width: 100% !important; padding: 0 !important; }
                        .container { width: 100% !important; border-radius: 0 !important; }
                        .content-pad { padding: 32px 24px !important; }
                        .header-pad { padding: 32px 24px !important; }
                        .btn-table { width: 100% !important; }
                        .btn-link { display: block !important; text-align: center !important; }
                        .footer-pad { padding: 20px 24px !important; }
                      }
                    </style>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #0f0f13; font-family: Georgia, 'Times New Roman', serif">
                    <div style="display: none; font-size: 1px; color: #0f0f13; line-height: 1px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden">
                      Recupera tu contraseña en Umbral con este enlace seguro.
                    </div>
                    <table class="wrapper" role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; padding: 48px 16px">
                      <tr>
                        <td align="center">
                          <table class="container" role="presentation" width="580" cellpadding="0" cellspacing="0" style="background-color: #16161d; border-radius: 16px; overflow: hidden; border: 1px solid #2a2a35">
                            <tr>
                              <td class="header-pad" style="padding: 48px 48px 40px; text-align: center; background-color: #16161d; border-bottom: 1px solid #2a2a35">
                                <table role="presentation" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto 20px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 12px; width: 52px; height: 52px; text-align: center; vertical-align: middle;">
                                      <span style="font-size: 24px; line-height: 52px; display: block">◈</span>
                                    </td>
                                  </tr>
                                </table>
                                <h1 style="margin: 0; font-family: Georgia, 'Times New Roman', serif; font-size: 26px; font-weight: 400; color: #e8c98a; letter-spacing: 6px; text-transform: uppercase;">UMBRAL</h1>
                                <p style="margin: 6px 0 0; font-family: Arial, sans-serif; font-size: 11px; color: #5a5a6e; letter-spacing: 3px; text-transform: uppercase">Finanzas Personales</p>
                              </td>
                            </tr>
                            <tr>
                              <td class="content-pad" style="padding: 48px">
                                <h2 style="margin: 0 0 16px; font-family: Georgia, 'Times New Roman', serif; font-size: 28px; font-weight: 400; color: #f0ece4; line-height: 1.3">
                                  Recupera tu acceso,<br /><span style="color: #c9a96e">{{firstName}}</span>
                                </h2>
                                <p style="margin: 0 0 12px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Recibimos una solicitud para restablecer tu contraseña en Umbral. Haz clic en el botón a continuación para crear una nueva contraseña.
                                </p>
                                <p style="margin: 0 0 36px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Si no solicitaste esto, puedes ignorar este correo con seguridad.
                                </p>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 36px">
                                  <tr>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                    <td style="padding: 0 16px; white-space: nowrap; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Restablecer contraseña</td>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                  </tr>
                                </table>
                                <table class="btn-table" role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 0 36px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 8px">
                                      <a class="btn-link" href="{{resetUrl}}" style="display: inline-block; padding: 16px 40px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #0f0f13; text-decoration: none; letter-spacing: 2px; text-transform: uppercase;">Restablecer contraseña</a>
                                    </td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; border-radius: 8px; border: 1px solid #2a2a35; margin-bottom: 36px">
                                  <tr>
                                    <td style="padding: 16px 20px">
                                      <p style="margin: 0 0 6px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Si el botón no funciona, usa este enlace:</p>
                                      <a href="{{resetUrl}}" style="font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #c9a96e; text-decoration: none; word-break: break-all">{{resetUrl}}</a>
                                    </td>
                                  </tr>
                                </table>
                                <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 13px; color: #4a4a5e; line-height: 1.6; border-left: 3px solid #2a2a35; padding-left: 16px;">
                                  Este enlace expira en <strong style="color: #6a6a7e">1 hora</strong> por razones de seguridad.
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td class="footer-pad" style="padding: 24px 48px; background-color: #0f0f13; border-top: 1px solid #1e1e28; text-align: center">
                                <p style="margin: 0 0 8px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">© {{year}} Umbral — Todos los derechos reservados</p>
                                <p style="margin: 0; font-family: Arial, sans-serif; font-size: 11px; color: #2a2a35">Este correo fue enviado a <span style="color: #4a4a5e">{{email}}</span></p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """;

        EmailTemplate template = EmailTemplate.builder()
                .templateKey("reset_password")
                .type(EmailTemplateType.RESET_PASSWORD)
                .subject("Recupera tu contraseña — Umbral")
                .htmlBodyCompressed(CompressionUtil.compress(htmlBody))
                .isActive(true)
                .build();

        emailTemplateRepository.save(template);
        logger.info("RESET_PASSWORD template initialized with template_key: reset_password");
    }

    private void initializeInvitationTemplate() throws Exception {
        if (emailTemplateRepository.findByTemplateKeyAndIsActiveTrue("invitation").isPresent()) {
            return;
        }

        String htmlBody = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <meta name="x-apple-disable-message-reformatting" />
                    <meta name="format-detection" content="telephone=no,address=no,email=no,date=no,url=no" />
                    <title>Te invitan a colaborar — Umbral</title>
                    <style>
                      #outlook a { padding: 0; }
                      body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                      table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                      img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
                      @media only screen and (max-width: 620px) {
                        .wrapper { width: 100% !important; padding: 0 !important; }
                        .container { width: 100% !important; border-radius: 0 !important; }
                        .content-pad { padding: 32px 24px !important; }
                        .header-pad { padding: 32px 24px !important; }
                        .btn-table { width: 100% !important; }
                        .btn-link { display: block !important; text-align: center !important; }
                        .footer-pad { padding: 20px 24px !important; }
                      }
                    </style>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #0f0f13; font-family: Georgia, 'Times New Roman', serif">
                    <div style="display: none; font-size: 1px; color: #0f0f13; line-height: 1px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden">
                      {{inviterName}} te invita a colaborar en Umbral.
                    </div>
                    <table class="wrapper" role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; padding: 48px 16px">
                      <tr>
                        <td align="center">
                          <table class="container" role="presentation" width="580" cellpadding="0" cellspacing="0" style="background-color: #16161d; border-radius: 16px; overflow: hidden; border: 1px solid #2a2a35">
                            <tr>
                              <td class="header-pad" style="padding: 48px 48px 40px; text-align: center; background-color: #16161d; border-bottom: 1px solid #2a2a35">
                                <table role="presentation" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto 20px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 12px; width: 52px; height: 52px; text-align: center; vertical-align: middle;">
                                      <span style="font-size: 24px; line-height: 52px; display: block">◈</span>
                                    </td>
                                  </tr>
                                </table>
                                <h1 style="margin: 0; font-family: Georgia, 'Times New Roman', serif; font-size: 26px; font-weight: 400; color: #e8c98a; letter-spacing: 6px; text-transform: uppercase;">UMBRAL</h1>
                                <p style="margin: 6px 0 0; font-family: Arial, sans-serif; font-size: 11px; color: #5a5a6e; letter-spacing: 3px; text-transform: uppercase">Finanzas Personales</p>
                              </td>
                            </tr>
                            <tr>
                              <td class="content-pad" style="padding: 48px">
                                <h2 style="margin: 0 0 16px; font-family: Georgia, 'Times New Roman', serif; font-size: 28px; font-weight: 400; color: #f0ece4; line-height: 1.3">
                                  Colabora con nosotros,<br /><span style="color: #c9a96e">{{firstName}}</span>
                                </h2>
                                <p style="margin: 0 0 12px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  <strong style="color: #c9a96e">{{inviterName}}</strong> te está invitando a colaborar en Umbral. Únete para gestionar tus finanzas en equipo y acceder a funcionalidades compartidas.
                                </p>
                                <p style="margin: 0 0 36px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Tienes 7 días para aceptar esta invitación. Haz clic en el botón a continuación para comenzar.
                                </p>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 36px">
                                  <tr>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                    <td style="padding: 0 16px; white-space: nowrap; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Aceptar invitación</td>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                  </tr>
                                </table>
                                <table class="btn-table" role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 0 36px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 8px">
                                      <a class="btn-link" href="{{invitationUrl}}" style="display: inline-block; padding: 16px 40px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #0f0f13; text-decoration: none; letter-spacing: 2px; text-transform: uppercase;">Aceptar invitación</a>
                                    </td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; border-radius: 8px; border: 1px solid #2a2a35; margin-bottom: 36px">
                                  <tr>
                                    <td style="padding: 16px 20px">
                                      <p style="margin: 0 0 6px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Si el botón no funciona, usa este enlace:</p>
                                      <a href="{{invitationUrl}}" style="font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #c9a96e; text-decoration: none; word-break: break-all">{{invitationUrl}}</a>
                                    </td>
                                  </tr>
                                </table>
                                <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 13px; color: #4a4a5e; line-height: 1.6; border-left: 3px solid #2a2a35; padding-left: 16px;">
                                  Puedes revisar la invitación en tu panel de control. Si no deseas colaborar, simplemente ignora este mensaje.
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td class="footer-pad" style="padding: 24px 48px; background-color: #0f0f13; border-top: 1px solid #1e1e28; text-align: center">
                                <p style="margin: 0 0 8px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">© {{year}} Umbral — Todos los derechos reservados</p>
                                <p style="margin: 0; font-family: Arial, sans-serif; font-size: 11px; color: #2a2a35">Este correo fue enviado a <span style="color: #4a4a5e">{{email}}</span></p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """;

        EmailTemplate template = EmailTemplate.builder()
                .templateKey("invitation")
                .type(EmailTemplateType.INVITATION)
                .subject("Te invitan a colaborar — Umbral")
                .htmlBodyCompressed(CompressionUtil.compress(htmlBody))
                .isActive(true)
                .build();

        emailTemplateRepository.save(template);
        logger.info("INVITATION template initialized with template_key: invitation");
    }

    private void initializeWelcomeTemplate() throws Exception {
        if (emailTemplateRepository.findByTemplateKeyAndIsActiveTrue("welcome").isPresent()) {
            return;
        }

        String htmlBody = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <meta name="x-apple-disable-message-reformatting" />
                    <meta name="format-detection" content="telephone=no,address=no,email=no,date=no,url=no" />
                    <title>¡Bienvenido a Umbral! — Finanzas Personales</title>
                    <style>
                      #outlook a { padding: 0; }
                      body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                      table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                      img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
                      @media only screen and (max-width: 620px) {
                        .wrapper { width: 100% !important; padding: 0 !important; }
                        .container { width: 100% !important; border-radius: 0 !important; }
                        .content-pad { padding: 32px 24px !important; }
                        .header-pad { padding: 32px 24px !important; }
                        .features { grid-template-columns: 1fr !important; }
                        .feature-item { padding: 24px 20px !important; }
                        .btn-table { width: 100% !important; }
                        .btn-link { display: block !important; text-align: center !important; }
                        .footer-pad { padding: 20px 24px !important; }
                      }
                    </style>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #0f0f13; font-family: Georgia, 'Times New Roman', serif">
                    <div style="display: none; font-size: 1px; color: #0f0f13; line-height: 1px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden">
                      Te damos la bienvenida a Umbral. Comienza a gestionar tus finanzas personales hoy.
                    </div>
                    <table class="wrapper" role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; padding: 48px 16px">
                      <tr>
                        <td align="center">
                          <table class="container" role="presentation" width="580" cellpadding="0" cellspacing="0" style="background-color: #16161d; border-radius: 16px; overflow: hidden; border: 1px solid #2a2a35">
                            <tr>
                              <td class="header-pad" style="padding: 48px 48px 40px; text-align: center; background-color: #16161d; border-bottom: 1px solid #2a2a35">
                                <table role="presentation" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto 20px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 12px; width: 52px; height: 52px; text-align: center; vertical-align: middle;">
                                      <span style="font-size: 24px; line-height: 52px; display: block">◈</span>
                                    </td>
                                  </tr>
                                </table>
                                <h1 style="margin: 0; font-family: Georgia, 'Times New Roman', serif; font-size: 26px; font-weight: 400; color: #e8c98a; letter-spacing: 6px; text-transform: uppercase;">UMBRAL</h1>
                                <p style="margin: 6px 0 0; font-family: Arial, sans-serif; font-size: 11px; color: #5a5a6e; letter-spacing: 3px; text-transform: uppercase">Finanzas Personales</p>
                              </td>
                            </tr>
                            <tr>
                              <td class="content-pad" style="padding: 48px">
                                <h2 style="margin: 0 0 16px; font-family: Georgia, 'Times New Roman', serif; font-size: 28px; font-weight: 400; color: #f0ece4; line-height: 1.3">
                                  ¡Bienvenido,<br /><span style="color: #c9a96e">{{firstName}}</span>!
                                </h2>
                                <p style="margin: 0 0 24px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Tu cuenta en Umbral ha sido activada exitosamente. Estamos entusiasmados de ayudarte a organizar y controlar tus finanzas personales con herramientas intuitivas y poderosas.
                                </p>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 36px">
                                  <tr>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                    <td style="padding: 0 16px; white-space: nowrap; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Características principales</td>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 36px">
                                  <tr>
                                    <td style="padding: 24px 20px; background-color: #0f0f13; border-radius: 8px; border: 1px solid #2a2a35; margin-bottom: 12px">
                                      <p style="margin: 0 0 4px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #e8c98a">📊 Dashboard Intuitivo</p>
                                      <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #8a8a9e">Visualiza tus gastos, ingresos y ahorros en tiempo real.</p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding: 24px 20px; background-color: #0f0f13; border-radius: 8px; border: 1px solid #2a2a35; margin-bottom: 12px">
                                      <p style="margin: 0 0 4px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #e8c98a">💰 Presupuestos Personalizados</p>
                                      <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #8a8a9e">Crea y administra presupuestos adaptados a tus necesidades.</p>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding: 24px 20px; background-color: #0f0f13; border-radius: 8px; border: 1px solid #2a2a35">
                                      <p style="margin: 0 0 4px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #e8c98a">👥 Colaboración en Equipo</p>
                                      <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #8a8a9e">Invita a otros para gestionar finanzas compartidas.</p>
                                    </td>
                                  </tr>
                                </table>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 36px">
                                  <tr>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                    <td style="padding: 0 16px; white-space: nowrap; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">Comienza ahora</td>
                                    <td style="border-top: 1px solid #2a2a35"></td>
                                  </tr>
                                </table>
                                <table class="btn-table" role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 0 36px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 8px">
                                      <a class="btn-link" href="{{appUrl}}" style="display: inline-block; padding: 16px 40px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #0f0f13; text-decoration: none; letter-spacing: 2px; text-transform: uppercase;">Ir a Umbral</a>
                                    </td>
                                  </tr>
                                </table>
                                <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 13px; color: #4a4a5e; line-height: 1.6; border-left: 3px solid #2a2a35; padding-left: 16px;">
                                  Si tienes preguntas o necesitas ayuda, estamos aquí para asistirte. Visita nuestro centro de soporte o responde a este correo.
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td class="footer-pad" style="padding: 24px 48px; background-color: #0f0f13; border-top: 1px solid #1e1e28; text-align: center">
                                <p style="margin: 0 0 8px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">© {{year}} Umbral — Todos los derechos reservados</p>
                                <p style="margin: 0; font-family: Arial, sans-serif; font-size: 11px; color: #2a2a35">Este correo fue enviado a <span style="color: #4a4a5e">{{email}}</span></p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """;

        EmailTemplate template = EmailTemplate.builder()
                .templateKey("welcome")
                .type(EmailTemplateType.WELCOME)
                .subject("¡Bienvenido a Umbral! — Finanzas Personales")
                .htmlBodyCompressed(CompressionUtil.compress(htmlBody))
                .isActive(true)
                .build();

        emailTemplateRepository.save(template);
        logger.info("WELCOME template initialized with template_key: welcome");
    }

    private void initializeBudgetAlertTemplate() throws Exception {
        if (emailTemplateRepository.findByTemplateKeyAndIsActiveTrue("budget_alert").isPresent()) {
            return;
        }

        String htmlBody = """
                <!doctype html>
                <html lang="es" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                    <meta name="x-apple-disable-message-reformatting" />
                    <meta name="format-detection" content="telephone=no,address=no,email=no,date=no,url=no" />
                    <title>Alerta de Presupuesto — Umbral</title>
                    <style>
                      #outlook a { padding: 0; }
                      body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
                      table, td { border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                      img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }
                      .warning-bar { background: linear-gradient(90deg, #d4521e 0%, #e8721e 100%); }
                      @media only screen and (max-width: 620px) {
                        .wrapper { width: 100% !important; padding: 0 !important; }
                        .container { width: 100% !important; border-radius: 0 !important; }
                        .content-pad { padding: 32px 24px !important; }
                        .header-pad { padding: 32px 24px !important; }
                        .btn-table { width: 100% !important; }
                        .btn-link { display: block !important; text-align: center !important; }
                        .footer-pad { padding: 20px 24px !important; }
                      }
                    </style>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #0f0f13; font-family: Georgia, 'Times New Roman', serif">
                    <div style="display: none; font-size: 1px; color: #0f0f13; line-height: 1px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden">
                      Alerta: Tu presupuesto en {{categoryName}} está por agotarse.
                    </div>
                    <table class="wrapper" role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; padding: 48px 16px">
                      <tr>
                        <td align="center">
                          <table class="container" role="presentation" width="580" cellpadding="0" cellspacing="0" style="background-color: #16161d; border-radius: 16px; overflow: hidden; border: 1px solid #2a2a35">
                            <tr class="warning-bar">
                              <td style="padding: 16px; text-align: center; background: linear-gradient(90deg, #d4521e 0%, #e8721e 100%)">
                                <p style="margin: 0; font-family: Arial, sans-serif; font-size: 13px; font-weight: 700; color: #ffffff; letter-spacing: 2px; text-transform: uppercase">⚠️ Alerta de Presupuesto</p>
                              </td>
                            </tr>
                            <tr>
                              <td class="header-pad" style="padding: 48px 48px 40px; text-align: center; background-color: #16161d; border-bottom: 1px solid #2a2a35">
                                <table role="presentation" cellpadding="0" cellspacing="0" align="center" style="margin: 0 auto 20px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 12px; width: 52px; height: 52px; text-align: center; vertical-align: middle;">
                                      <span style="font-size: 24px; line-height: 52px; display: block">◈</span>
                                    </td>
                                  </tr>
                                </table>
                                <h1 style="margin: 0; font-family: Georgia, 'Times New Roman', serif; font-size: 26px; font-weight: 400; color: #e8c98a; letter-spacing: 6px; text-transform: uppercase;">UMBRAL</h1>
                                <p style="margin: 6px 0 0; font-family: Arial, sans-serif; font-size: 11px; color: #5a5a6e; letter-spacing: 3px; text-transform: uppercase">Finanzas Personales</p>
                              </td>
                            </tr>
                            <tr>
                              <td class="content-pad" style="padding: 48px">
                                <h2 style="margin: 0 0 16px; font-family: Georgia, 'Times New Roman', serif; font-size: 28px; font-weight: 400; color: #f0ece4; line-height: 1.3">
                                  Alerta de presupuesto,<br /><span style="color: #e8721e">{{firstName}}</span>
                                </h2>
                                <p style="margin: 0 0 12px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Tu presupuesto para <strong style="color: #e8c98a">{{categoryName}}</strong> está por agotarse.
                                </p>
                                <p style="margin: 0 0 24px; font-family: Arial, Helvetica, sans-serif; font-size: 15px; color: #8a8a9e; line-height: 1.7">
                                  Has gastado <strong style="color: #e8c98a">{{spent}}</strong> de un presupuesto de <strong style="color: #e8c98a">{{limit}}</strong>. Quedan solo <strong style="color: #e8721e">{{remaining}}</strong>.
                                </p>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color: #0f0f13; border-radius: 8px; border-left: 4px solid #e8721e; padding: 16px; margin-bottom: 36px">
                                  <tr>
                                    <td>
                                      <p style="margin: 0 0 8px; font-family: Arial, sans-serif; font-size: 12px; color: #8a8a9e">Desglose:</p>
                                      <table role="presentation" width="100%" cellpadding="0" cellspacing="0">
                                        <tr>
                                          <td style="padding: 6px 0; font-family: Arial, sans-serif; font-size: 13px; color: #c9a96e">Límite:</td>
                                          <td style="padding: 6px 0; font-family: Arial, sans-serif; font-size: 13px; color: #f0ece4; text-align: right">{{limit}}</td>
                                        </tr>
                                        <tr>
                                          <td style="padding: 6px 0; font-family: Arial, sans-serif; font-size: 13px; color: #8a8a9e">Gastado:</td>
                                          <td style="padding: 6px 0; font-family: Arial, sans-serif; font-size: 13px; color: #f0ece4; text-align: right">{{spent}}</td>
                                        </tr>
                                        <tr style="border-top: 1px solid #2a2a35">
                                          <td style="padding: 6px 0; font-family: Arial, sans-serif; font-size: 13px; color: #e8721e; font-weight: 700">Disponible:</td>
                                          <td style="padding: 6px 0; font-family: Arial, sans-serif; font-size: 13px; color: #e8721e; text-align: right; font-weight: 700">{{remaining}}</td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
                                <table class="btn-table" role="presentation" cellpadding="0" cellspacing="0" style="margin: 0 0 36px">
                                  <tr>
                                    <td style="background: linear-gradient(135deg, #c9a96e 0%, #e8c98a 50%, #c9a96e 100%); border-radius: 8px">
                                      <a class="btn-link" href="{{dashboardUrl}}" style="display: inline-block; padding: 16px 40px; font-family: Arial, Helvetica, sans-serif; font-size: 13px; font-weight: 700; color: #0f0f13; text-decoration: none; letter-spacing: 2px; text-transform: uppercase;">Ver Presupuesto</a>
                                    </td>
                                  </tr>
                                </table>
                                <p style="margin: 0; font-family: Arial, Helvetica, sans-serif; font-size: 13px; color: #4a4a5e; line-height: 1.6; border-left: 3px solid #2a2a35; padding-left: 16px;">
                                  Revisa tu presupuesto en el dashboard para hacer ajustes o aumentar el límite si es necesario.
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td class="footer-pad" style="padding: 24px 48px; background-color: #0f0f13; border-top: 1px solid #1e1e28; text-align: center">
                                <p style="margin: 0 0 8px; font-family: Arial, sans-serif; font-size: 11px; color: #3a3a4a; letter-spacing: 2px; text-transform: uppercase">© {{year}} Umbral — Todos los derechos reservados</p>
                                <p style="margin: 0; font-family: Arial, sans-serif; font-size: 11px; color: #2a2a35">Este correo fue enviado a <span style="color: #4a4a5e">{{email}}</span></p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """;

        EmailTemplate template = EmailTemplate.builder()
                .templateKey("budget_alert")
                .type(EmailTemplateType.BUDGET_ALERT)
                .subject("Alerta de Presupuesto — {{categoryName}} — Umbral")
                .htmlBodyCompressed(CompressionUtil.compress(htmlBody))
                .isActive(true)
                .build();

        emailTemplateRepository.save(template);
        logger.info("BUDGET_ALERT template initialized with template_key: budget_alert");
    }
}
