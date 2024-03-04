package com.example.oauth2.auth.email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String name;
    private String password;
}
