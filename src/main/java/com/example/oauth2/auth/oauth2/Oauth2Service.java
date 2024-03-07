package com.example.oauth2.auth.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.oauth2.authprovider.AuthProvider;
import com.example.oauth2.authprovider.AuthUserProviderService;
import com.example.oauth2.entity.User;
import com.example.oauth2.user.UserService;

@Service
@RequiredArgsConstructor
public class Oauth2Service extends DefaultOAuth2UserService {
    private final UserService userService;
    private final AuthUserProviderService authUserProviderService;
    private final GitHubService gitHubService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        //The implementation it gets at runtime is DefaultOAuth2user
        OAuth2User oAuth2User = super.loadUser(userRequest);
        var githubEmail = this.gitHubService.getGitHubEmail(userRequest.getAccessToken().getTokenValue());
        var authProvider = AuthProvider.valueOf(userRequest.getClientRegistration()
                .getClientName()
                .toUpperCase());
        var authUserProvider = this.authUserProviderService.findByAuthProvider(authProvider);

        User user;
        var optionalUser = this.userService.findByEmail(githubEmail.email());

        if(optionalUser.isEmpty()) {
            user = this.userService.registerOidcUser(oAuth2User, githubEmail.email(), authUserProvider);
        } else {
            user = optionalUser.get();
            user = this.userService.updateOauth2User(user, authUserProvider);
        }

        return new SSOUser(user);
    }
}
