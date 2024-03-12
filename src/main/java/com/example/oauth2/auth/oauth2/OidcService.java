package com.example.oauth2.auth.oauth2;

import com.example.oauth2.authprovider.AuthProviderType;
import com.example.oauth2.entity.User;
import com.example.oauth2.authprovider.AuthProviderService;
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
    private final AuthProviderService authProviderService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        var authProvider = AuthProviderType.valueOf(userRequest.getClientRegistration()
                .getClientName()
                .toUpperCase());
        var authUserProvider = this.authProviderService.findByAuthProviderType(authProvider);

        User user;
        var optionalUser = this.userService.findByEmail(oidcUser.getEmail());

        /*
            If we wanted more properties from the Oauth2 user we could pass the whole object. Since OidcUser extends
            Oauth2User we could pass oidcUser as well with no problem.
         */
        if(optionalUser.isEmpty()) {
            user = this.userService.registerOauth2User(oidcUser.getAttribute("name"),
                    oidcUser.getEmail(),
                    authUserProvider
            );
        } else {
            user = optionalUser.get();
            user = this.userService.updateOauth2User(user, oidcUser.getAttribute("name"), authUserProvider);
        }

        return new SSOUser(user);
    }
}
