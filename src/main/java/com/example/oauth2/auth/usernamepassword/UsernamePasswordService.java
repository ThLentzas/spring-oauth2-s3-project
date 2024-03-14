package com.example.oauth2.auth.usernamepassword;

import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.authprovider.AuthProviderType;
import com.example.oauth2.authprovider.AuthProviderService;
import com.example.oauth2.entity.User;
import com.example.oauth2.token.PasswordResetTokenService;
import com.example.oauth2.token.dto.PasswordResetRequest;
import com.example.oauth2.user.UserService;
import com.example.oauth2.email.EmailService;
import com.example.oauth2.token.UserActivationTokenService;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsernamePasswordService {
    private final UserService userService;
    private final AuthProviderService authProviderService;
    private final UserActivationTokenService userVerificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    /*
        The account will be in a "not activated" state where the user is asked to activate it whenever they visit the
        page, and there's a link to send an activation email, so they can still login, but they can't do certain things
        unless their account is activated. We still set the SPRING_SECURITY_CONTEXT_KEY of the current session, but
        for subsequent requests for the user to be authenticated is not enough, the account has to be activated.
     */
    public void registerUser(RegisterRequest registerRequest, HttpSession session) {
        var authProvider = this.authProviderService.findByAuthProviderType(AuthProviderType.EMAIL);
        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();

        user = this.userService.registerUsernamePasswordUser(user, authProvider);
        if(!user.isEnabled()) {
            var token = this.userVerificationTokenService.createAccountActivationToken(user);
            this.emailService.sendAccountActivationEmail(user.getEmail(), user.getName(), token.getTokenValue());
        } else {
            this.passwordResetTokenService.createPasswordResetToken(new PasswordResetRequest(user.getEmail()), true);
        }

        setupSessionSpringSecurityContext(user, session);
    }

    /*
        For the form login, the Authentication object is of type UsernamePasswordAuthenticationToken, and for OAuth2,
        it is of OAuth2AuthenticationToken
     */
    private void setupSessionSpringSecurityContext(User user, HttpSession session) {
        var usernamePasswordUser = new UsernamePasswordUser(user);
        var authentication = new UsernamePasswordAuthenticationToken(
                usernamePasswordUser,
                null,
                usernamePasswordUser.getAuthorities()
        );

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
