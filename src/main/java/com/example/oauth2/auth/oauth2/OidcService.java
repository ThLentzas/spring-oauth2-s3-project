package com.example.oauth2.auth.oauth2;

import com.example.oauth2.entity.User;
import com.example.oauth2.authprovider.AuthUserProviderService;
import com.example.oauth2.authprovider.AuthProvider;
import com.example.oauth2.user.UserService;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OidcService extends OidcUserService {
    private final UserService userService;
    private final AuthUserProviderService authUserProviderService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        var authProvider = AuthProvider.valueOf(userRequest.getClientRegistration()
                .getClientName()
                .toUpperCase());
        var authUserProvider = this.authUserProviderService.findByAuthProvider(authProvider);

        User user;
        var optionalUser = this.userService.findByEmail(oidcUser.getEmail());

        if(optionalUser.isEmpty()) {
            user = this.userService.registerOauth2User(oidcUser, authUserProvider);
        } else {
            user = optionalUser.get();
            user = this.userService.updateOauth2User(user, authUserProvider);
        }

        return new SSOUser(user);
    }
}
