package com.example.oauth2.token.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank(message = "The Email field is required") String email) {
}
