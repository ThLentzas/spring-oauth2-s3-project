package com.example.oauth2.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.example.oauth2.socialaccount.dto.SocialAccountDTO;

@Getter
@Setter
public class UserProfile {
    private String name;
    private List<SocialAccountDTO> socialAccounts;
    private boolean enabled;

    public UserProfile() {
    }

    public UserProfile(String name, List<SocialAccountDTO> socialAccounts) {
        this.name = name;
        this.socialAccounts = socialAccounts;
    }
}
