package com.example.oauth2.entity;

import com.example.oauth2.auth.OAuth2Provider;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
public class OAuth2UserProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private OAuth2Provider oAuth2Provider;
}
