package com.example.oauth2.utils;

import java.security.SecureRandom;
import java.util.Base64;

public final class TokenUtils {

    private TokenUtils() {

        // prevent instantiation
        throw new UnsupportedOperationException("TokenUtils is a utility class and cannot be instantiated");
    }

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[128];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}
