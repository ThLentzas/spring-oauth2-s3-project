package com.example.oauth2.entity;

import com.example.oauth2.authprovider.AuthProvider;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "providers")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class AuthUserProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AuthProvider authProvider;
}
