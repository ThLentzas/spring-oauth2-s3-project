package com.example.oauth2.auth.oauth2;

import com.example.oauth2.auth.oauth2.dto.GithubEmail;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GitHubService {
    private final RestClient restClient;

    public GitHubService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .build();
    }

    public GithubEmail getGitHubEmail(String accessToken) {
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
