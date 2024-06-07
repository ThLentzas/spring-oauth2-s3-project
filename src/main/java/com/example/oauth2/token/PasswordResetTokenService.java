package com.example.oauth2.token;

import com.example.oauth2.authprovider.AuthProviderType;
import com.example.oauth2.email.EmailService;
import com.example.oauth2.entity.PasswordResetToken;
import com.example.oauth2.entity.User;
import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;
import com.example.oauth2.token.dto.PasswordResetRequest;
import com.example.oauth2.user.UserService;
import com.example.oauth2.utils.PasswordUtils;
import com.example.oauth2.utils.TokenUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetTokenService.class);

    /*
        findByEmail() is a custom method we created, and it's not annotated with @Transactional. It's a read, so it is
        safe to execute in an auto-commit mode because they do not modify the state of the database. When executed, the
        underlying JDBC connection (if not already part of an explicit transaction) operates in auto-commit mode,
        effectively wrapping each individual read operation in a transaction that is immediately committed upon
        completion. On the other hand when we call deleteTokensByUser() also a custom method not annotated
        @Transactional we attempt a modifying operation without an active transaction, JPA/Hibernate expects these
        operations to be wrapped in a transaction to manage these potential side effects properly. If JPA detects a
        modifying operation being executed without an active transaction, it throws a TransactionRequiredException to
        signal that the operation violates the requirement for transactional integrity. So we either annotate the method
        that calls deleteTokensByUser() with @Transactional, or we annotate the method itself
     */
    @Transactional
    public void createPasswordResetToken(PasswordResetRequest passwordResetRequest, boolean linking) {
        if (linking) {
            this.userService.findByEmail(passwordResetRequest.getEmail()).ifPresent(user -> {
                PasswordResetToken token = generatePasswordResetToken(user);
                this.emailService.sendAccountRegistrationLinkingEmail(passwordResetRequest.getEmail(),
                        user.getName(),
                        token.getTokenValue());
            });
            return;
        }

        this.userService.findByEmailAndProvider(passwordResetRequest.getEmail(), AuthProviderType.EMAIL).ifPresent(
                userAuthProvider -> {
                    PasswordResetToken token = generatePasswordResetToken(userAuthProvider.getUser());
                    this.emailService.sendPasswordResetEmail(passwordResetRequest.getEmail(), token.getTokenValue());
                });
    }

    public boolean resetPassword(String tokenValue, PasswordResetConfirmationRequest request) {
        Optional<PasswordResetToken> tokenOptional = verifyToken(tokenValue);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new BadCredentialsException("Passwords don't match");
        }

        PasswordResetToken passwordResetToken = tokenOptional.get();
        PasswordUtils.validatePassword(request.getNewPassword());
        passwordResetToken.getUser().setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        this.userService.save(passwordResetToken.getUser());
        this.passwordResetTokenRepository.delete(passwordResetToken);

        return true;
    }

    public Optional<PasswordResetToken> verifyToken(String tokenValue) {
        if (tokenValue.isBlank()) {
            logger.info("Received empty password reset token");

            return Optional.empty();
        }

        Optional<PasswordResetToken> tokenOptional = this.passwordResetTokenRepository.findByTokenValue(tokenValue);
        if (tokenOptional.isEmpty()) {
            logger.info("Password reset token not found for token value: {}", tokenValue);

            return tokenOptional;
        }

        PasswordResetToken passwordResetToken = tokenOptional.get();
        if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
            logger.info("Password reset link expired for user with id: {}", passwordResetToken.getUser().getId());
            this.passwordResetTokenRepository.delete(passwordResetToken);

            return Optional.empty();
        }

        return tokenOptional;
    }

    private PasswordResetToken generatePasswordResetToken(User user) {
        String token = TokenUtils.generateToken();
        Instant expiryDate = Instant.now().plus(3, ChronoUnit.HOURS);
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, expiryDate);

        this.passwordResetTokenRepository.deleteTokensByUser(user);
        this.passwordResetTokenRepository.save(passwordResetToken);

        return passwordResetToken;
    }
}
