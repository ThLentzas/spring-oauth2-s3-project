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
import com.example.oauth2.authprovider.AuthProvider;

import java.io.Serializable;

/*
    The reason why both AuthUserProvider and User Entities have to implement Serializable is because they are part of
    the authentication object of the Security Context that is stored in Redis as the value of the
    SPRING_SECURITY_CONTEXT KEY
 */
@Entity
@Table(name = "providers")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class AuthUserProvider implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AuthProvider authProvider;
}
