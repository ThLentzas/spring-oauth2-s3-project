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

import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsernamePasswordService {
    private final UserService userService;
    private final AuthProviderService authProviderService;
    private final UserActivationTokenService userVerificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    //https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#use-securitycontextholderstrategy
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    /*
        The account will be in a "not activated" state where the user is asked to activate it whenever they visit the
        page, and there's a link to send an activation email, so they can still login, but they can't do certain things
        unless their account is activated. We still set the SPRING_SECURITY_CONTEXT_KEY of the current session, but
        for subsequent requests for the user to be authenticated is not enough, the account has to be activated(user
        having the role VERIFIED).

        TOTAL QUERIES: 4 => 1.findByAuthProvider(), 2. findByEmail(), 3. save() for user 4.save() for provider by
        performing a SELECT before to ensure the composed PK is unique in the join table.
     */
    @Transactional
    public void registerUser(RegisterRequest registerRequest,
                             HttpServletRequest servletRequest,
                             HttpServletResponse servletResponse) {
        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();
        var authProvider = this.authProviderService.findByAuthProviderType(AuthProviderType.EMAIL);
        user = this.userService.registerUsernamePasswordUser(user, authProvider);
        /*
            Case: If the user's providers list is equal to 1 and that one is an EMAIL provider, it means that the user
            didn't log in with some oauth2 provider using the same email prior to its current registration. In that case
            we send an account activation email. Otherwise, it means that they have already logged in with an oauth2
            provider with the same email, so for account linking they should go through password reset.(linking
            the username/password account they just created with the oauth2 account that already exists under the same
            email)
         */
        if (user.getUserAuthProviders().size() == 1 && user.getUserAuthProviders().stream()
                .anyMatch(userAuthProvider ->
                        userAuthProvider.getAuthProvider().getAuthProviderType().equals(AuthProviderType.EMAIL))) {
            var token = this.userVerificationTokenService.createAccountActivationToken(user);
            this.emailService.sendAccountActivationEmail(user.getEmail(), user.getName(), token.getTokenValue());
        } else {
            this.passwordResetTokenService.createPasswordResetToken(new PasswordResetRequest(user.getEmail()), true);
        }

        setupSessionSpringSecurityContext(user, servletRequest, servletResponse);
    }

    /*
        For the form login, the Authentication object is of type UsernamePasswordAuthenticationToken, and for OAuth2,
        it is of OAuth2AuthenticationToken

        https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html
     */
    private void setupSessionSpringSecurityContext(User user,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        var usernamePasswordUser = new UsernamePasswordUser(user);
        var authentication = new UsernamePasswordAuthenticationToken(
                usernamePasswordUser,
                null,
                usernamePasswordUser.getAuthorities()
        );

        //https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#use-securitycontextholderstrategy
        var context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        this.securityContextRepository.saveContext(context, request, response);
    }
}
