package com.example.oauth2.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordService;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.token.PasswordResetTokenService;
import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {
    private final UsernamePasswordService usernamePasswordService;
    private final PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/register")
    ResponseEntity<Void> registerUser(@ModelAttribute("registerRequest") RegisterRequest request, HttpSession session) {
        this.usernamePasswordService.registerUser(request, session);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/password_reset/confirm")
    String confirmPasswordReset(@RequestParam(name = "token", defaultValue = "") String token,
                                @Valid @ModelAttribute("passwordResetConfirmationRequest")
                                PasswordResetConfirmationRequest request,
                                RedirectAttributes redirectAttributes) {
        boolean updated = this.passwordResetTokenService.resetPassword(token, request);
        if(updated) {
            return "redirect:/login";
        }

        return "redirect:/error";
    }
}
