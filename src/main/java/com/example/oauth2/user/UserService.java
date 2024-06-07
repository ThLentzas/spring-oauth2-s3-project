package com.example.oauth2.user;

import com.example.oauth2.authprovider.AuthProviderType;
import com.example.oauth2.entity.AuthProvider;
import com.example.oauth2.entity.UserActivationToken;
import com.example.oauth2.entity.UserAuthProvider;
import com.example.oauth2.entity.key.UserAuthProviderKey;
import com.example.oauth2.exception.ServerErrorException;
import com.example.oauth2.token.UserActivationTokenService;
import com.example.oauth2.entity.User;
import com.example.oauth2.exception.DuplicateResourceException;
import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.email.EmailService;
import com.example.oauth2.user.dto.UserProfile;
import com.example.oauth2.user.dto.UserProfileMapper;
import com.example.oauth2.utils.PasswordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final UserActivationTokenService userActivationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserProfileMapper userProfileMapper = new UserProfileMapper();
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /*
        We set both the userAuthProvider to the user and the user to the userAuthProvider because the relationship is
        bidirectional

        Despite passing the user as an argument and updating it we still have to return the user. The reason is in the
        case of a user already existing with the provided email via some oauth2 provider we have to update the
        relationship, but we call user = tmp.get(); and now the copy of the user reference we passed no longer points
        to the same object as the original so changing the user at this point no longer affects the original object.

        Case 1: User does not exist, we create the user and the provider is EMAIL
        Case 2: User exists with the same email and provider to be EMAIL, 409 conflict
        Case 3: User exists with the same email under different provider(GOOGLE, GITHUB), we create the relationship
        in the join table.
     */
    public User registerUsernamePasswordUser(User user, AuthProvider authProvider) {
        validateUser(user);
        Optional<User> tmp = this.userRepository.findByEmail(user.getEmail());

        /*
            We are passing null, because the auth_provider_user_id in the case of the EMAIL provider will be the user's
            id(PK), but the user is not persisted yet.
         */
        if (tmp.isEmpty()) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.ROLE_USER);
            user.setLastSignedInAt(Instant.now());
            persistUserAuthDetails(user, null, authProvider, false);

            return user;
        }

        user = tmp.get();
        boolean matched = user.getUserAuthProviders().stream()
                .anyMatch(userAuthProvider ->
                        userAuthProvider.getAuthProvider().getAuthProviderType().equals(AuthProviderType.EMAIL));
        if (matched) {
            throw new DuplicateResourceException("Email already in use.");
        }

        UserAuthProvider userAuthProvider = createUserAuthProvider(user,
                user.getEmail(),
                user.getName(),
                String.valueOf(user.getId()),
                authProvider,
                false
        );
        this.userAuthProviderRepository.save(userAuthProvider);
        user.getUserAuthProviders().add(userAuthProvider);
        user.setLastSignedInAt(Instant.now());

        return this.userRepository.save(user);
    }

    public User registerOauth2User(String name, String email, String authProviderUserId, AuthProvider authProvider) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(UUID.randomUUID().toString())
                .role(Role.ROLE_VERIFIED)
                .verifiedAt(Instant.now())
                .lastSignedInAt(Instant.now())
                .build();
        persistUserAuthDetails(user, authProviderUserId, authProvider, true);

        return user;
    }

    /*
        The reason we are not updating the auth provider's user id when the provider already exists for the user, and
        we only keep track of the latest name they have with that provider is because the id never changes

        Case: If the user already exists for the given provider we make sure that we have the latest name for that user
        based on the provider. Otherwise, we create the relationship between the user and the auth provider. When a
        user is authenticated via an oauth2 provider is immediately verified and their account is activated, that's why
        we pass true
     */
    public User updateOauth2User(User user, OAuth2User oAuth2User, AuthProvider authProvider) {
        Optional<UserAuthProvider> userAuthProviderOptional = user.getUserAuthProviders().stream()
                .filter(tmp -> tmp.getAuthProvider().getAuthProviderType().equals(authProvider.getAuthProviderType()))
                .findFirst();

        String name;
        String authProviderUserId;
        if (oAuth2User instanceof OidcUser) {
            name = oAuth2User.getAttribute("name");
            authProviderUserId = oAuth2User.getAttribute("sub");
        } else {
            name = oAuth2User.getAttribute("login");
            authProviderUserId = Objects.requireNonNull(oAuth2User.getAttribute("id")).toString();
        }

        if (userAuthProviderOptional.isPresent()) {
            UserAuthProvider userAuthProvider = userAuthProviderOptional.get();
            userAuthProvider.setAuthProviderName(name);
            user.setLastSignedInAt(Instant.now());
            this.userAuthProviderRepository.save(userAuthProvider);
            this.userRepository.save(user);

            return user;
        }

        UserAuthProvider userAuthProvider = new UserAuthProvider(
                new UserAuthProviderKey(user.getId(), authProvider.getId()),
                user,
                authProvider,
                user.getEmail(),
                name,
                authProviderUserId,
                true
        );
        user.getUserAuthProviders().add(userAuthProvider);
        user.setLastSignedInAt(Instant.now());
        this.userAuthProviderRepository.save(userAuthProvider);

        return this.userRepository.save(user);
    }

    /*
        We throw 500, because we are searching for the current authenticated user to make sure it
        exists in the database. If the user is not found based on the id of the current authenticated user we have
        an issue with our database data. I don't know if this check should be done in the first place.

        User user = this.userRepository.findById(usernamePasswordUser.user().getId()).orElseThrow(() -> {
            logger.warn("User not found with id: {}", usernamePasswordUser.user().getId());
            return new ServerErrorException(SERVER_ERROR_MSG);
        });

        The reason why we call emailService with usernamePasswordUser.user().getEmail() etc, and not with
        user.getEmail() is because since we are using getReferenceById, a proxy is created with a property set that is
        the entity's id, and we use that for linking. If later we call user.getEmail() a query will be fired to fetch
        the actual entity since we are accessing one of its properties other than the id
     */
    @Transactional
    void activateUserAccount(UsernamePasswordUser usernamePasswordUser) {
        User user = this.userRepository.getReferenceById(usernamePasswordUser.user().getId());
        this.userActivationTokenService.deleteTokensByUser(user);
        UserActivationToken token = this.userActivationTokenService.createAccountActivationToken(user);
        this.emailService.sendAccountActivationEmail(usernamePasswordUser.user().getEmail(),
                usernamePasswordUser.user().getEmail(),
                token.getTokenValue());
    }

    /*
        This method gets called when the user clicks the link from their email to activate their account. We give the
        user a new role, delete the token and also update the status for the auth provider. When auth provider is the
        EMAIL, which is the only case where we will request the user to activate their account via email, we also have
        to update the enabled column in the users_auth_providers table where provider is EMAIL. When we stream the
        providers for that user, and we do not find a provider of type EMAIL it means there is a data integrity problem
        in our db.
     */
    @Transactional
    boolean verifyUser(String tokenValue) {
        Optional<UserActivationToken> tokenOptional = this.userActivationTokenService.verifyToken(tokenValue);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        UserActivationToken token = tokenOptional.get();
        UserAuthProvider userAuthProvider = token.getUser().getUserAuthProviders().stream()
                .filter(provider -> provider.getAuthProvider().getAuthProviderType().equals(AuthProviderType.EMAIL))
                .findFirst()
                .orElseThrow(() -> {
                    logger.info("EMAIL provider was not found during account activation for user with id: {}",
                            token.getUser().getId());
                    return new ServerErrorException("The server encountered an internal error and was unable to complete your request. Please try again later");
                });
        userAuthProvider.setEnabled(true);

        token.getUser().setRole(Role.ROLE_VERIFIED);
        token.getUser().setVerifiedAt(Instant.now());
        this.userRepository.save(token.getUser());
        this.userActivationTokenService.delete(token);
        this.userAuthProviderRepository.save(userAuthProvider);

        return true;
    }

    /*
        If the principal of authentication object is of type UsernamePassword, we have to know if that user has or not
        activated their profile to render the correct buttons in the user profile.
     */
    public UserProfile findByIdFetchingSocialAccounts(Long id, Authentication authentication) {
        User user = this.userRepository.findByIdFetchingSocialAccounts(id).orElseThrow(() -> {
            logger.info("User record was not found for the id of the current authenticated user: {}", id);
            return new ServerErrorException("The server encountered an internal error and was unable to complete your request. Please try again later");
        });

        UserProfile userProfile = this.userProfileMapper.apply(user);
        if (authentication.getPrincipal() instanceof UsernamePasswordUser) {
            boolean enabled = user.getUserAuthProviders().stream()
                    .filter(userAuthProvider ->
                            userAuthProvider.getAuthProvider().getAuthProviderType().equals(AuthProviderType.EMAIL))
                    .map(UserAuthProvider::isEnabled)
                    .findFirst()
                    .orElse(true);
            if (Boolean.FALSE.equals(enabled)) {
                userProfile.setEnabled(false);
                return userProfile;
            }
        }
        userProfile.setEnabled(true);

        return userProfile;
    }

    public void updateLastSignedInAt(User user) {
        user.setLastSignedInAt(Instant.now());
        this.userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Optional<UserAuthProvider> findByEmailAndProvider(String email, AuthProviderType authProviderType) {
        return this.userAuthProviderRepository.findByEmailAndProvider(email, authProviderType);
    }

    public void save(User user) {
        this.userRepository.save(user);
    }

    private void validateUser(User user) {
        validateEmail(user.getEmail());
        PasswordUtils.validatePassword(user.getPassword());
    }

    private void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private UserAuthProvider createUserAuthProvider(User user,
                                                    String email,
                                                    String name,
                                                    String authProviderUserId,
                                                    AuthProvider authProvider,
                                                    boolean enabled) {
        return new UserAuthProvider(
                new UserAuthProviderKey(user.getId(), authProvider.getId()),
                user,
                authProvider,
                email,
                name,
                authProviderUserId,
                enabled
        );
    }

    private void persistUserAuthDetails(User user,
                                        String authProviderUserId,
                                        AuthProvider authProvider,
                                        boolean enabled) {
        this.userRepository.save(user);
        if (authProviderUserId == null) {
            authProviderUserId = user.getId().toString();
        }

        UserAuthProvider userAuthProvider = createUserAuthProvider(user,
                user.getEmail(),
                user.getName(),
                authProviderUserId,
                authProvider,
                enabled
        );
        Set<UserAuthProvider> userAuthProviders = Set.of(userAuthProvider);
        user.setUserAuthProviders(userAuthProviders);
        this.userAuthProviderRepository.save(userAuthProvider);
    }
}
