package com.example.oauth2.entity;

import com.example.oauth2.user.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.io.Serializable;
import java.util.Set;

/*
    The reason why both AuthUserProvider and User Entities have to implement Serializable is because they are part of
    the authentication object of the Security Context that is stored in Redis as the value of the
    SPRING_SECURITY_CONTEXT KEY
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}, name = "unique_users_email")
})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Role role;
    @Column(nullable = false)
    private boolean enabled;
    @ManyToMany
    @JoinTable(
            name = "users_providers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "provider_id")
    )
    private Set<AuthUserProvider> authUserProviders;

    public User(String name, String email, String password, Set<AuthUserProvider> authUserProviders) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.authUserProviders = authUserProviders;
    }

    public User() {
        enabled = false;
    }
}
