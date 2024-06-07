package com.example.oauth2.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.example.oauth2.exception.ServerErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final ThymeleafService thymeleafService;
    @Value("${spring.mail.username}")
    private String sender;

    @Async
    public void sendAccountActivationEmail(String recipient, String username, String token) {
        String activationLink = String.format("http://localhost:8080/api/v1/user/verify?token=%s", token);
        String context = this.thymeleafService.setAccountVerificationEmailContext(activationLink, username);

        sendEmail(recipient, "Activate your account", context);
    }

    @Async
    public void sendAccountRegistrationLinkingEmail(String recipient, String username, String token) {
        String tokenLink = "http://localhost:8080/password_reset/confirm?token=" + token;
        String passwordResetLink = "http://localhost:8080/password_reset";
        String context = this.thymeleafService.setAccountRegistrationLinkingEmailContext(username, tokenLink, passwordResetLink);

        sendEmail(recipient, "Link your account", context);
    }

    @Async
    public void sendPasswordResetEmail(String recipient, String token) {
        String tokenLink = "http://localhost:8080/password_reset/confirm?token=" + token;
        String passwordResetLink = "http://localhost:8080/password_reset";
        String context = thymeleafService.setPasswordResetEmailContext(tokenLink, passwordResetLink);

        sendEmail(recipient, "Reset your Test password", context);
    }

    private void sendEmail(String recipient, String subject, String emailContext) {
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(recipient);
            helper.setFrom(sender);
            helper.setSubject(subject);
            helper.setText(emailContext, true);

            this.mailSender.send(mimeMessage);
        } catch (MessagingException me) {
            throw new ServerErrorException("The server encountered an internal error and was unable to complete your " +
                    "request. Please try again later");
        }
    }
}
