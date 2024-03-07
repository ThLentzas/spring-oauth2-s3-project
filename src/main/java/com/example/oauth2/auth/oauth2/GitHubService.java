package com.example.oauth2.auth.oauth2;

import com.example.oauth2.auth.oauth2.dto.GithubEmail;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
class GitHubService {
    private final RestClient restClient;

    GitHubService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .build();
    }

    GithubEmail getGitHubEmail(String accessToken) {
        List<GithubEmail> emails =  this.restClient.get()
                .uri("/user/emails")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        return emails.stream().filter(GithubEmail::primary)
                .findFirst()
                .orElseThrow();
    }
}
