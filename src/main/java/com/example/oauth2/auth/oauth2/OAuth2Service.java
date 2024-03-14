package com.example.oauth2.auth.oauth2;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.oauth2.authprovider.AuthProviderType;
import com.example.oauth2.authprovider.AuthProviderService;
import com.example.oauth2.entity.User;
import com.example.oauth2.user.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService {
    private final UserService userService;
    private final AuthProviderService authProviderService;
    private final GitHubService gitHubService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        //The implementation it gets at runtime is DefaultOAuth2user
        OAuth2User oAuth2User = super.loadUser(userRequest);
        var githubEmail = this.gitHubService.getGitHubEmail(userRequest.getAccessToken().getTokenValue());
        var authProviderType = AuthProviderType.valueOf(userRequest.getClientRegistration()
                .getClientName()
                .toUpperCase());
        var authProvider = this.authProviderService.findByAuthProviderType(authProviderType);
        User user;
        var optionalUser = this.userService.findByEmail(githubEmail.email());

        /*
            If we wanted more properties from the Oauth2 user we could pass the whole object. We can't call
            oAuth2User.getAttribute("name") because name can be null while login can not when provider is GitHub.
         */
        if(optionalUser.isEmpty()) {
            user = this.userService.registerOauth2User(oAuth2User.getAttribute("login"),
                    githubEmail.email(),
                    Objects.requireNonNull(oAuth2User.getAttribute("id")).toString(),
                    authProvider
            );
        } else {
            user = optionalUser.get();
            user = this.userService.updateOauth2User(user,oAuth2User, authProvider);
        }

        return new SSOUser(user);
    }
}
