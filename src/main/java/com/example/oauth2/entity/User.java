package com.example.oauth2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

import com.example.oauth2.user.Role;

/*
    The reason why User has to implement Serializable is, because its part of the UsernamePasswordUser that implements
    UserDetails, and it is the principal of the authentication object of the Security Context that is stored in Redis
    as the value of the SPRING_SECURITY_CONTEXT KEY. The Authentication object itself implements Serializable as well
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}, name = "unique_users_email")
})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String profileImageKey;
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
    @CreatedDate
    private Instant createdAt;
    private Instant verifiedAt;
    @Column(nullable = false)
    private Instant lastSignedInAt;
    @OneToMany(mappedBy = "user")
    private Set<UserAuthProvider> userAuthProviders;
    @OneToMany(mappedBy = "user")
    private Set<SocialAccount> socialAccounts;
}
