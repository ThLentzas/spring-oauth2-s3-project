package com.example.oauth2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.example.oauth2.authprovider.AuthProviderType;

import java.io.Serializable;

/*
    The reason why AuthProvider has to implement Serializable is, because its part of the UserAuthProvider object that
    is part of the User that is part of the UsernamePasswordUser that implements UserDetails, and it is the principal of
    the authentication object of the Security Context that is stored in Redis as the value of the
    SPRING_SECURITY_CONTEXT KEY. The Authentication object itself implements Serializable as well
 */
@Entity
@Table(name = "providers")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class AuthProvider implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AuthProviderType authProviderType;
}
