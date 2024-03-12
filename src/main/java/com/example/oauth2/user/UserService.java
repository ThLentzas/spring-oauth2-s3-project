package com.example.oauth2.user;

import com.example.oauth2.authprovider.AuthProviderType;
import com.example.oauth2.entity.AuthProvider;
import com.example.oauth2.entity.UserAuthProvider;
import com.example.oauth2.entity.key.UserAuthProviderKey;
import com.example.oauth2.token.UserActivationTokenService;
import com.example.oauth2.entity.User;
import com.example.oauth2.exception.DuplicateResourceException;
import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.email.EmailService;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final UserActivationTokenService userVerificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /*
        We set both the userAuthProvider to the user and the user to the userAuthProvider because the relationship is
        bidirectional

        Despite passing the user as an argument and updating it we still have to return the user. The reason is in the
        case of a user already existing with the provided email via some oauth2 provider we have to update the
        relationship, but we call user = tmp.get(); and now the copy of the user reference we passed no longer points
        to the same object as the original so changing the user at this point no longer affects the original object.
     */
    public User registerUsernamePasswordUser(User user, AuthProvider authProvider) {
        validateUser(user);
        var tmp = this.userRepository.findByEmail(user.getEmail());

        //toDo: count the queries
        if (tmp.isEmpty()) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            user.setLastSignedInAt(Instant.now());
            persistUserAuthDetails(user, authProvider);

            return user;
        }

        user = tmp.get();
        var matched = user.getUserAuthProviders().stream()
                .anyMatch(userAuthProvider ->
                        userAuthProvider.getAuthProvider().getAuthProviderType().equals(AuthProviderType.EMAIL));
        if (matched) {
            throw new DuplicateResourceException("Email already in use.");
        }

        var userAuthProvider = createUserAuthProvider(user, user.getEmail(), user.getName(), authProvider);
        this.userAuthProviderRepository.save(userAuthProvider);
        user.getUserAuthProviders().add(userAuthProvider);
        user.setLastSignedInAt(Instant.now());

        return this.userRepository.save(user);
    }

    public User registerOauth2User(String name, String email, AuthProvider authProvider) {
        var user = User.builder()
                .name(name)
                .email(email)
                .password(UUID.randomUUID().toString())
                .enabled(true)
                .role(Role.VERIFIED)
                .verifiedAt(Instant.now())
                .lastSignedInAt(Instant.now())
                .build();
        persistUserAuthDetails(user, authProvider);

        return user;
    }

    public User updateOauth2User(User user, String name, AuthProvider authProvider) {
        var userAuthProviderOptional = user.getUserAuthProviders().stream()
                .filter(tmp -> tmp.getAuthProvider().getAuthProviderType().equals(authProvider.getAuthProviderType()))
                .findFirst();

        if (userAuthProviderOptional.isPresent()) {
            UserAuthProvider userAuthProvider = userAuthProviderOptional.get();
            userAuthProvider.setName(name);
            this.userAuthProviderRepository.save(userAuthProvider);

            return user;
        }

        var userAuthProvider = new UserAuthProvider(
                new UserAuthProviderKey(user.getId(), authProvider.getId()),
                user,
                authProvider,
                user.getEmail(),
                name
        );
        user.getUserAuthProviders().add(userAuthProvider);
        this.userAuthProviderRepository.save(userAuthProvider);

        return this.userRepository.save(user);
    }

    /*
        We throw 500, because we are searching for the current authenticated user to make sure it
        exists in the database. If the user is not found based on the id of the current authenticated user we have
        an issue with our database data. I don't know if this check should be done in the first place.

        var user = this.userRepository.findById(usernamePasswordUser.user().getId()).orElseThrow(() -> {
            logger.warn("User not found with id: {}", usernamePasswordUser.user().getId());
            return new ServerErrorException(SERVER_ERROR_MSG);
        });

        The reason why we call emailService with usernamePasswordUser.user().getEmail() etc, and not with
        user.getEmail() is because since we are using getReferenceById, a proxy is created with a property set that is
        the entity's id, and we use that for linking. If later we call user.getEmail() a query will be fired to fetch
        the actual entity since we are accessing one of its properties other than the id

     */
    void activateUserAccount(UsernamePasswordUser usernamePasswordUser) {
        var user = this.userRepository.getReferenceById(usernamePasswordUser.user().getId());
        this.userVerificationTokenService.deleteAllTokens(user);
        var token = this.userVerificationTokenService.createAccountActivationToken(user);
        this.emailService.sendAccountActivationEmail(usernamePasswordUser.user().getEmail(),
                usernamePasswordUser.user().getEmail(),
                token.getTokenValue());

    }

    boolean verifyUser(String tokenValue) {
        var tokenOptional = this.userVerificationTokenService.verifyToken(tokenValue);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        var token = tokenOptional.get();
        token.getUser().setEnabled(true);
        token.getUser().setRole(Role.VERIFIED);
        token.getUser().setVerifiedAt(Instant.now());
        this.userRepository.save(token.getUser());

        return true;
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    private void validateUser(User user) {
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
    }

    private void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
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

    private UserAuthProvider createUserAuthProvider(User user, String email, String name, AuthProvider authProvider) {
        return new UserAuthProvider(
                new UserAuthProviderKey(user.getId(), authProvider.getId()),
                user,
                authProvider,
                email,
                name
        );
    }

    private void persistUserAuthDetails(User user, AuthProvider authProvider) {
        var userAuthProvider = createUserAuthProvider(user, user.getEmail(), user.getName(), authProvider);
        Set<UserAuthProvider> userAuthProviders = Set.of(userAuthProvider);
        user.setUserAuthProviders(userAuthProviders);

        this.userRepository.save(user);
        this.userAuthProviderRepository.save(userAuthProvider);
    }
}
