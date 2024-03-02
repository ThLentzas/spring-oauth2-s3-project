package com.example.oauth2.auth;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.example.oauth2.entity.User;
import com.example.oauth2.user.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OidcService extends OidcUserService {
    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        User user = new User();
        user.setName(oidcUser.getName());
        user.setEmail(oidcUser.getEmail());
        user.setPassword(UUID.randomUUID().toString());

        this.userRepository.save(user);

        return new SSOUser(user);
    }
}
