package com.example.oauth2.user;

import com.example.oauth2.entity.AuthUserProvider;
import com.example.oauth2.entity.User;
import com.example.oauth2.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void registerEmailAuthUser(User user) {
        if(this.userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already in use. If you already have an account with Google/Github go through the password reset");
        }

        this.userRepository.save(user);
    }

    public User registerOauth2User(OidcUser oidcUser, AuthUserProvider authUserProvider) {
        User user = new User();
        user.setName(oidcUser.getAttribute("name"));
        user.setEmail(oidcUser.getEmail());
        user.setPassword(UUID.randomUUID().toString());
        user.setEnabled(true);
        user.setAuthUserProviders(Set.of(authUserProvider));

        return this.userRepository.save(user);
    }

    public User updateOauth2User(User user, AuthUserProvider authUserProvider) {
        user.getAuthUserProviders().add(authUserProvider);

        return this.userRepository.save(user);
    }

    public User registerOidcUser(OAuth2User oAuth2User, String email, AuthUserProvider authUserProvider) {
        User user = new User();
        user.setName(oAuth2User.getAttribute("login"));
        user.setEmail(email);
        user.setPassword(UUID.randomUUID().toString());
        user.setEnabled(true);
        user.setAuthUserProviders(Set.of(authUserProvider));

        return this.userRepository.save(user);
    }

    public void validateUser(User user) {
        validateEmail(user.getEmail());
        validateEmail(user.getPassword());
    }

    private void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        PasswordValidator validator = new PasswordValidator(
                new LengthRule(12, 128),
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

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
