package com.example.oauth2.auth.usernamepassword;

import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.authprovider.AuthProvider;
import com.example.oauth2.authprovider.AuthUserProviderService;
import com.example.oauth2.entity.AuthUserProvider;
import com.example.oauth2.entity.User;
import com.example.oauth2.user.UserService;
import com.example.oauth2.email.EmailService;
import com.example.oauth2.entity.UserActivationToken;
import com.example.oauth2.token.UserActivationTokenService;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsernamePasswordService {
    private final UserService userService;
    private final AuthUserProviderService authUserProviderService;
    private final UserActivationTokenService userVerificationTokenService;
    private final EmailService emailService;

    /*
        The account will be in a "not activated" state where the user is asked to activate it whenever they visit the
        page, and there's a link to send an activation email, so they can still login, but they can't do certain things
        unless their account is activated. We still set the SPRING_SECURITY_CONTEXT_KEY of the current session, but
        for subsequent requests for the user to be authenticated is not enough, the account has to be activated.
     */
    public void registerUser(RegisterRequest registerRequest, HttpSession session) {
        AuthUserProvider authUserProvider = this.authUserProviderService.findByAuthProvider(AuthProvider.EMAIL);
        User user = new User(registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                Set.of(authUserProvider)
        );

        this.userService.registerUsernamePasswordUser(user);
        UserActivationToken token = this.userVerificationTokenService.createAccountActivationToken(user);
        this.emailService.sendAccountActivationEmail(user.getEmail(), user.getName(), token.getTokenValue());

        setupSessionSpringSecurityContext(user, session);
    }

    private void setupSessionSpringSecurityContext(User user, HttpSession session) {
        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                usernamePasswordUser,
                null,
                usernamePasswordUser.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
