package com.example.oauth2.auth.oauth2.dto;

public record GithubEmail(String email, boolean verified, boolean primary, String visibility) {
}
