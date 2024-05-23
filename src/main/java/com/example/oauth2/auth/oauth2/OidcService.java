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

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OidcService extends OidcUserService {
    private final UserService userService;
    private final AuthProviderService authProviderService;

    /*
        The idToken is always a JWT and the subject of the token is the id the user has in the authProvider. For Google
        the subject is the GoogleId the user has for Google, and it will always be the same for that provider

        When we call registerOauth2User() with oidcUser.getAttribute("sub") it's the same as calling it with
        oidcUser.getIdToken().getClaims().get("sub")); The claim sub of the idToken holds the auth provider's user id.

        The OidcUser(extends OAuth2user) is the user created based on that information retrieved from the
        OAuth2Provider. The return value is the Principal of the Authentication. The Authentication is of type
        OAuth2AuthenticationToken

        https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html
     */
    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        var authProvider = AuthProviderType.valueOf(userRequest.getClientRegistration()
                .getClientName()
                .toUpperCase());
        var authUserProvider = this.authProviderService.findByAuthProviderType(authProvider);

        User user;
        var optionalUser = this.userService.findByEmail(oidcUser.getAttribute("email"));

        /*
            If we wanted more properties from the Oauth2 user we could pass the whole object. Since OidcUser extends
            Oauth2User we could pass oidcUser as well with no problem.
         */
        if(optionalUser.isEmpty()) {
            user = this.userService.registerOauth2User(oidcUser.getAttribute("name"),
                    oidcUser.getEmail(),
                    oidcUser.getAttribute("sub"),
                    authUserProvider
            );
        } else {
            user = optionalUser.get();
            user = this.userService.updateOauth2User(user, oidcUser, authUserProvider);
        }

        return new SocialLoginUser(user);
    }
}
