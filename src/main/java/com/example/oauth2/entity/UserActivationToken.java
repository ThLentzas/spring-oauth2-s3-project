package com.example.oauth2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_activation_tokens")
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class UserActivationToken {
    @Id
    private Long id;
    /*
        Share PK strategy. We are basically telling Hibernate to map the id of the current Entity to a column of another
        In our case it's the PK of the user named "id". If we had a composed key as PK of the current entity, we would
        have to specify which property of the composed PK we would map. @MapsId(name = "userId") and the composed PK
        would have a property named userId.
     */
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
