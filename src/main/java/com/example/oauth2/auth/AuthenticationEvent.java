package com.example.oauth2.auth;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.user.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticationEvent {
    private final UserService userService;

    /*
        When the user is authenticated via the form login we have to update the lastSignedInAt property of that user
        https://docs.spring.io/spring-security/reference/servlet/authentication/events.html
     */
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        if (Objects.requireNonNull(success.getAuthentication().getPrincipal()) instanceof UsernamePasswordUser u) {
            this.userService.updateLastSignedInAt(u.user());
        }
    }
}
