package com.example.oauth2.auth;

import com.example.oauth2.auth.email.dto.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequest request) {

        return "redirect:/xd";
    }
}
