package com.example.oauth2.entity;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_activation_tokens")
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class UserActivationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    private User user;
    @Column(nullable = false)
    private String tokenValue;
    @Column(nullable = false)
    private Instant expiryDate;

    public UserActivationToken() {
    }

    public UserActivationToken(User user, String tokenValue, Instant expiryDate) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
    }
}
