package com.example.oauth2.token;

import com.example.oauth2.entity.UserActivationToken;
import com.example.oauth2.entity.User;
import com.example.oauth2.exception.ServerErrorException;
import com.example.oauth2.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserActivationTokenService {
    private final UserActivationTokenRepository userActivationTokenRepository;

    public UserActivationToken createAccountVerificationToken(User user) {
        String token = TokenUtils.generateToken();
        Instant expiryTime = Instant.now().plus(1, ChronoUnit.DAYS);
        UserActivationToken userActivationToken = new UserActivationToken(user, token, expiryTime);

        return this.userActivationTokenRepository.save(userActivationToken);
    }

    public UserActivationToken findByTokenValue(String tokenValue) {
        return this.userActivationTokenRepository.findByTokenValue(tokenValue).orElseThrow(() ->
                new ServerErrorException("The server encountered an internal error and was unable to complete your request. Please try again later"));
    }
}
