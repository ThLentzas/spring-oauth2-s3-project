package com.example.oauth2.email;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ThymeleafService {
    private final TemplateEngine templateEngine;

    String setAccountVerificationEmailContext(String activationLink, String username) {
        Context context = new Context();
        context.setVariable("activationLink", activationLink);
        context.setVariable("username", username);

        return this.templateEngine.process("account_activation_email", context);
    }

    String setAccountRegistrationLinkingEmailContext(String username, String tokenLink, String passwordResetLink) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("tokenLink", tokenLink);
        context.setVariable("passwordResetLink", passwordResetLink);

        return this.templateEngine.process("account_linking_email", context);
    }

    String setPasswordResetEmailContext(String tokenLink, String passwordResetLink) {
        Context context = new Context();
        context.setVariable("tokenLink", tokenLink);
        context.setVariable("passwordResetLink", passwordResetLink);

        return templateEngine.process("password_reset_email", context);
    }
}
