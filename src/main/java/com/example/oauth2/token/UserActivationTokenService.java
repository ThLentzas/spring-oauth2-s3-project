package com.example.oauth2.token;

import com.example.oauth2.entity.UserActivationToken;
import com.example.oauth2.entity.User;
import com.example.oauth2.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserActivationTokenService {
    private final UserActivationTokenRepository userActivationTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserActivationTokenService.class);

    public UserActivationToken createAccountActivationToken(User user) {
        String token = TokenUtils.generateToken();
        Instant expiryTime = Instant.now().plus(1, ChronoUnit.DAYS);
        UserActivationToken userActivationToken = new UserActivationToken(user, token, expiryTime);

        return this.userActivationTokenRepository.save(userActivationToken);
    }

    /*
        This method validates the token that's encoded in the activation link. Since we can't really throw exceptions
        we keep a state on the server for each invalid case by logging, and we return false for each one.

        Note: ifPresentOrElse() would not work because we cannot return inside a lamda. orElseThrow() would also not
        work because we can't throw an exception, since we are validating the request that is made by clicking the link
     */
    public Optional<UserActivationToken> verifyToken(String tokenValue) {
        if(tokenValue.isBlank()) {
            logger.info("Received empty user activation token");

            return Optional.empty();
        }

        Optional<UserActivationToken> tokenOptional = this.userActivationTokenRepository.findByTokenValue(tokenValue);
        if(tokenOptional.isEmpty()) {
            logger.info("User activation token not found for token value: {}", tokenValue);

            return tokenOptional;
        }

        UserActivationToken userActivationToken = tokenOptional.get();
        if(userActivationToken.getExpiryDate().isBefore(Instant.now())) {
            logger.info("User activation link expired for user with id: {}", userActivationToken.getUser().getId());
            this.userActivationTokenRepository.delete(userActivationToken);

            return Optional.empty();
        }

        return tokenOptional;
    }
}
