package com.example.oauth2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    private String name;
    private String password;
    private boolean enabled;
    @ManyToMany
    private Set<OAuth2UserProvider> oAuth2Providers;
}
