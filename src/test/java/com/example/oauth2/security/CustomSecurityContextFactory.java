package com.example.oauth2.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.example.oauth2.auth.oauth2.SocialLoginUser;
import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.entity.User;
import com.example.oauth2.user.Role;

import java.util.UUID;

/*
    Since we are creating a class that implements the UserDetails, we need to provide our own annotation for the mock
    user, because the default one provider by Spring Security is not enough in this case, we might have extra properties
    in our class. Creating that annotation will allow us to covert that to a custom mock spring security context that
    will use the custom authentication object.

    https://www.youtube.com/watch?v=onD_fyhy58o&list=PLEocw3gLFc8X_a8hGWGaBnSkPFJmbb8QP&index=39
 */
public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    /*
        This is the logic behind our custom annotation. We use the default values of our annotation and for the role we
        pass the value specified @WithMockCustomUser(roles = "USER"). If we had more roles we have to adjust
        the logic
     */
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext testSecurityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication;
        User user = new User();
        user.setId(1L);
        user.setName(annotation.username());
        user.setPassword(annotation.password());
        user.setRole(Role.valueOf("ROLE_" + annotation.roles()[0]));

        if(annotation.socialLogin()) {
            SocialLoginUser principal = new SocialLoginUser(user);
            authentication = new OAuth2AuthenticationToken(
                    principal,
                    principal.getAuthorities(),
                    UUID.randomUUID().toString()
            );
        } else {
            UsernamePasswordUser principal = new UsernamePasswordUser(user);
            authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );
        }
        testSecurityContext.setAuthentication(authentication);

        return testSecurityContext;
    }
}