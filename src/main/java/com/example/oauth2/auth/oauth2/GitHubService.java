package com.example.oauth2.auth.oauth2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.oauth2.exception.ServerErrorException;
import com.example.oauth2.auth.oauth2.dto.GithubEmail;

import java.util.List;

@Service
class GitHubService {
    private final RestClient restClient;
    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

    GitHubService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .build();
    }

    /*
        Even if we have access to the user information when GitHub is the provider the value of the email in the
        response can be null if the user has their email private. Based on their docs we have to make a subsequent
        request with the scope user:email and their access token to the /user/emails

        https://stackoverflow.com/questions/35373995/github-user-email-is-null-despite-useremail-scope
        https://docs.github.com/en/rest/users/emails?apiVersion=2022-11-28#list-email-addresses-for-a-user

        Based on GitHub's settings every user has a primary email
     */
    GithubEmail getGitHubEmail(String accessToken) {
        List<GithubEmail> emails = this.restClient.get()
                .uri("/user/emails")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return emails.stream().filter(GithubEmail::primary)
                .findFirst()
                .orElseThrow(() -> {
                    logger.info("No primary email found");

                    return new ServerErrorException("The server encountered an internal error and was unable to" +
                            "complete your request. Please try again later");
                });
    }
}
