package com.example.oauth2.auth;

import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }
}
