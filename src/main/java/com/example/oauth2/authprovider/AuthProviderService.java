package com.example.oauth2.authprovider;

import com.example.oauth2.entity.AuthProvider;
import com.example.oauth2.exception.ServerErrorException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthProviderService {
    private final AuthProviderRepository authProviderRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthProviderService.class);
    private static final String SERVER_ERROR_MSG = "The server encountered an internal error and was unable to " +
            "complete your request. Please try again later";

    public AuthProvider findByAuthProviderType(AuthProviderType authProviderType) {
        return this.authProviderRepository.findByAuthProvider(authProviderType)
                .orElseThrow(() -> {
                    logger.info("AuthUserProvider was not found with authProvider: {}", authProviderType);

                    return new ServerErrorException(SERVER_ERROR_MSG);
                });
    }
}
