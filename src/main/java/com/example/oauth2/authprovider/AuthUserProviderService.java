package com.example.oauth2.authprovider;

import com.example.oauth2.entity.AuthUserProvider;
import com.example.oauth2.exception.ServerErrorException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserProviderService {
    private final AuthUserProviderRepository authUserProviderRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthUserProviderService.class);
    private static final String SERVER_ERROR_MSG = "The server encountered an internal error and was unable to " +
            "complete your request. Please try again later";

    public AuthUserProvider findByAuthProvider(AuthProvider authProvider) {
        return this.authUserProviderRepository.findByAuthProvider(authProvider)
                .orElseThrow(() -> {
                    logger.info("AuthUserProvider was not found with authProvider: {}", authProvider);

                    return new ServerErrorException(SERVER_ERROR_MSG);
                });
    }
}
