package com.example.oauth2.utils;

import org.passay.*;

public final class PasswordUtils {
    private PasswordUtils() {

        // prevent instantiation
        throw new UnsupportedOperationException("PasswordUtils is a utility class and cannot be instantiated");
    }

    public static void validatePassword(String password) {
        var validator = new PasswordValidator(new LengthRule(12, 128),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
        );

        RuleResult result = validator.validate(new PasswordData(password));
        if (!result.isValid()) {
            throw new IllegalArgumentException(validator.getMessages(result).get(0));
        }
    }
}
