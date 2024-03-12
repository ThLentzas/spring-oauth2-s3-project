package com.example.oauth2.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import com.example.oauth2.entity.key.UserAuthProviderKey;

import java.io.Serializable;

/*
    The reason why UserAuthProvider has to implement Serializable is, because its part of the User that is part of the
    UsernamePasswordUser that implements UserDetails, and it is the principal of the authentication object of the
    Security Context that is stored in Redis as the value of the SPRING_SECURITY_CONTEXT KEY. The Authentication object
    itself implements Serializable as well
 */
@Entity
@Table(name = "users_providers")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserAuthProvider implements Serializable {
    /*
        If you provide your own id value then Spring Data will assume that you need to check the DB for a duplicate
        key (hence the select+insert).
     */
    @EmbeddedId
    private UserAuthProviderKey id;
    /*
        We are basically telling Hibernate to map the id of the current Entity to a column of another
        Since the PK of the UserAuthProvider is a composed PK we have to map each property of the id.

        @MapsId("userId")
        @JoinColumn(name = "id") => Maps the property "userId" of the composed id with the id of the user and the column
         in the table users_providers is called user_id

        @MapsId("providerId")
        @JoinColumn(name = "id") => Maps the property "providerId" of the composed id with the id of the provider and
        the column in the table users_providers is called provider_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("providerId")
    @JoinColumn(name = "provider_id")
    private AuthProvider authProvider;
    private String email;
    private String name;

    public UserAuthProvider() {
    }

    public UserAuthProvider(UserAuthProviderKey id, User user, AuthProvider authProvider, String email, String name) {
        this.id = id;
        this.user = user;
        this.authProvider = authProvider;
        this.email = email;
        this.name = name;
    }
}
