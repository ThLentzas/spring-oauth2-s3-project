package com.example.oauth2.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.token.dto.PasswordResetRequest;

@Controller
class AuthViewController {

    @GetMapping("/login")
    String loginView() {
        return "login";
    }

    @GetMapping("/register")
    String registrationView(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }

    @GetMapping("/account_activation")
    String accountActivationView() {
        return "account_activation";
    }

    @GetMapping("/password_reset")
    String passwordResetView(Model model) {
        model.addAttribute("passwordResetRequest", new PasswordResetRequest());

        return "password_reset";
    }

    @GetMapping("/password_reset/confirm")
    String passwordResetConfirmationView(Model model) {
        model.addAttribute("passwordResetConfirmationRequest", new PasswordResetConfirmationRequest());

        return "password_reset_confirm";
    }
}
