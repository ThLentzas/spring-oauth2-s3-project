package com.example.oauth2.auth.dto;

public record GithubEmail(String email, boolean verified, boolean primary, String visibility) {
}
