package com.example.oauth2.auth.email;

import com.example.oauth2.auth.email.dto.RegisterRequest;
import com.example.oauth2.authprovider.AuthProvider;
import com.example.oauth2.authprovider.AuthUserProviderService;
import com.example.oauth2.entity.AuthUserProvider;
import com.example.oauth2.entity.User;
import com.example.oauth2.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final UserService userService;
    private final AuthUserProviderService authUserProviderService;
    private final PasswordEncoder passwordEncoder;

    void registerEmailAuthUser(RegisterRequest registerRequest) {
        AuthUserProvider authUserProvider = this.authUserProviderService.findByAuthProvider(AuthProvider.EMAIL);
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setAuthUserProviders(Set.of(authUserProvider));

        this.userService.validateUser(user);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        this.userService.registerEmailAuthUser(user);
    }

}
