package com.example.oauth2.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;

@Controller
class AuthViewController {

    @GetMapping("/register")
    String registrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }

    @GetMapping("/password_reset/confirm")
    String passwordResetConfirmationForm(Model model) {
        model.addAttribute("passwordResetConfirmationRequest", new PasswordResetConfirmationRequest());

        return "password_reset_confirm";
    }
}
