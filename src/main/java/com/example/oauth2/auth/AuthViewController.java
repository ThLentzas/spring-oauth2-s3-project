package com.example.oauth2.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.token.dto.PasswordResetRequest;

//toDo: Explain why we only have POST and GET requests
@Controller
class AuthViewController {
    @GetMapping("/login")
    String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    String registrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }

    @GetMapping("/password_reset")
    String passwordResetForm(Model model) {
        model.addAttribute("passwordResetRequest", new PasswordResetRequest());

        return "password_reset";
    }


    @GetMapping("/password_reset/confirm")
    String passwordResetConfirmationForm(Model model) {
        model.addAttribute("passwordResetConfirmationRequest", new PasswordResetConfirmationRequest());

        return "password_reset_confirm";
    }
}
