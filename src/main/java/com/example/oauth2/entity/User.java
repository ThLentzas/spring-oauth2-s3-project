package com.example.oauth2.entity;

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

import java.io.Serializable;
import java.util.Set;

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
    private boolean activated;
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
        activated = false;
    }
}
