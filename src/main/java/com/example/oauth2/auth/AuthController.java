package com.example.oauth2.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordService;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsernamePasswordService usernamePasswordService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@ModelAttribute RegisterRequest request, HttpSession session) {
        this.usernamePasswordService.registerUser(request, session);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
