package com.example.oauth2.email;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThymeleafService {
    private final TemplateEngine templateEngine;

    String setAccountVerificationEmailContext(String activationLink, String username) {
        Context context = new Context();
        context.setVariable("activationLink", activationLink);
        context.setVariable("username", username);

        return this.templateEngine.process("account_verification_email", context);
    }
}
