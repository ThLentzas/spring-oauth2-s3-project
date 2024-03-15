package com.example.oauth2.token.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetConfirmationRequest {
    private String newPassword;
    private String confirmationPassword;
}
