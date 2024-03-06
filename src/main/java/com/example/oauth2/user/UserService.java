package com.example.oauth2.user;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.oauth2.entity.UserActivationToken;
import com.example.oauth2.token.UserActivationTokenService;
import com.example.oauth2.entity.AuthUserProvider;
import com.example.oauth2.entity.User;
import com.example.oauth2.exception.DuplicateResourceException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserActivationTokenService userVerificationTokenService;
    private final PasswordEncoder passwordEncoder;

    public void registerUsernamePasswordUser(User user) {
        validateUser(user);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
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
        user.setActivated(true);
        user.setAuthUserProviders(Set.of(authUserProvider));

        return this.userRepository.save(user);
    }

    public User registerOidcUser(OAuth2User oAuth2User, String email, AuthUserProvider authUserProvider) {
        User user = new User();
        user.setName(oAuth2User.getAttribute("login"));
        user.setEmail(email);
        user.setPassword(UUID.randomUUID().toString());
        user.setActivated(true);
        user.setAuthUserProviders(Set.of(authUserProvider));

        return this.userRepository.save(user);
    }

    public User updateOauth2User(User user, AuthUserProvider authUserProvider) {
        user.getAuthUserProviders().add(authUserProvider);

        return this.userRepository.save(user);
    }

    boolean activateUserAccount(String tokenValue) {
        Optional<UserActivationToken> tokenOptional = this.userVerificationTokenService.verifyToken(tokenValue);
        if(tokenOptional.isEmpty()) {
            return false;
        }

        UserActivationToken token = tokenOptional.get();
        token.getUser().setActivated(true);
        this.userRepository.save(token.getUser());

        return true;
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void validateUser(User user) {
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
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
}
