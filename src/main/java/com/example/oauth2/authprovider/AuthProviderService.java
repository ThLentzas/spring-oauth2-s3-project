package com.example.oauth2.authprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.oauth2.entity.AuthProvider;
import com.example.oauth2.exception.ServerErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthProviderService {
    private final AuthProviderRepository authProviderRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthProviderService.class);

    public AuthProvider findByAuthProviderType(AuthProviderType authProviderType) {
        return this.authProviderRepository.findByAuthProvider(authProviderType)
                .orElseThrow(() -> {
                    logger.info("AuthUserProvider was not found with authProvider: {}", authProviderType);
                    return new ServerErrorException("The server encountered an internal error and was unable to complete your request. Please try again later");
                });
    }
}
