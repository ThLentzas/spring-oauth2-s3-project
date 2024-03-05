package com.example.oauth2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_activation_tokens")
@Setter
@Getter
public class UserActivationToken {
    @Id
    private Long id;
    //Shared PK strategy
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
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
