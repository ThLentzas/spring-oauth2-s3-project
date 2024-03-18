package com.example.oauth2.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordService;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.token.PasswordResetTokenService;
import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;
import com.example.oauth2.token.dto.PasswordResetRequest;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {
    private final UsernamePasswordService usernamePasswordService;
    private final PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/register")
    String registerUser(@ModelAttribute("registerRequest") RegisterRequest request, HttpSession session) {
        this.usernamePasswordService.registerUser(request, session);

        return "redirect:/account_activation";
    }

    @PostMapping("/password_reset")
    String confirmPasswordReset(@Valid @ModelAttribute("passwordResetRequest")
                                PasswordResetRequest request,
                                RedirectAttributes redirectAttributes) {
        this.passwordResetTokenService.createPasswordResetToken(request, false);
        redirectAttributes.addFlashAttribute("passwordResetGenericResponse", true);

        return "redirect:/login";
    }

    @PostMapping("/password_reset/confirm")
    String confirmPasswordReset(@RequestParam(name = "token", defaultValue = "") String token,
                                @Valid @ModelAttribute("passwordResetConfirmationRequest")
                                PasswordResetConfirmationRequest request,
                                RedirectAttributes redirectAttributes) {
        boolean updated = this.passwordResetTokenService.resetPassword(token, request);
        if (updated) {
            redirectAttributes.addFlashAttribute("passwordResetSuccess", true);
            return "redirect:/login";
        }

        return "redirect:/error";
    }
}
