package com.example.oauth2.user.dto;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String name;
    private MultipartFile profileImage;
}
