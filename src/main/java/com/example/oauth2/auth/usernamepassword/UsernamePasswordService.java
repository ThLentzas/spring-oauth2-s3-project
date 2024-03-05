package com.example.oauth2.auth.usernamepassword;

import com.example.oauth2.email.EmailService;
import com.example.oauth2.entity.UserActivationToken;
import com.example.oauth2.token.UserActivationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.authprovider.AuthProvider;
import com.example.oauth2.authprovider.AuthUserProviderService;
import com.example.oauth2.entity.AuthUserProvider;
import com.example.oauth2.entity.User;
import com.example.oauth2.user.UserService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsernamePasswordService {
    private final UserService userService;
    private final AuthUserProviderService authUserProviderService;
    private final UserActivationTokenService userVerificationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest registerRequest) {
        AuthUserProvider authUserProvider = this.authUserProviderService.findByAuthProvider(AuthProvider.EMAIL);
        User user = new User(registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                Set.of(authUserProvider)
        );

        this.userService.validateUser(user);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        this.userService.registerUsernamePasswordUser(user);
        UserActivationToken token = this.userVerificationTokenService.createAccountVerificationToken(user);
        this.emailService.sendAccountVerificationEmail(user.getEmail(), user.getName(), token.getTokenValue());
    }
}
