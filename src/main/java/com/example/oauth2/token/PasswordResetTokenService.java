package com.example.oauth2.token;

import com.example.oauth2.email.EmailService;
import com.example.oauth2.entity.PasswordResetToken;
import com.example.oauth2.token.dto.PasswordResetRequest;
import com.example.oauth2.user.UserService;
import com.example.oauth2.utils.TokenUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final UserService userService;

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
        this.userService.findByEmail(passwordResetRequest.email()).ifPresent(user -> {
            var token = TokenUtils.generateToken();
            var expiryDate = Instant.now().plus(3, ChronoUnit.HOURS);
            var passwordResetToken = new PasswordResetToken(user, token, expiryDate);

            this.passwordResetTokenRepository.deleteTokensByUser(user);
            this.passwordResetTokenRepository.save(passwordResetToken);

            if (linking) {
                this.emailService.sendAccountRegistrationLinkingEmail(passwordResetRequest.email(), user.getName(), token);
                return;
            }
            this.emailService.sendPasswordResetEmail(passwordResetRequest.email(), token);
        });
    }
}
