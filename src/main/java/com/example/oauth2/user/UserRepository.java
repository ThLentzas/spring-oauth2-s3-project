package com.example.oauth2.user;

import com.example.oauth2.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oauth2.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /*
        The query means if the user is found with a given email always fetch its auth providers. A user always has at
        least 1 provider.
     */
    @Query("""
                SELECT u
                FROM User u
                JOIN FETCH u.userAuthProviders uap
                JOIN FETCH uap.authProvider
                WHERE u.email = :email
            """)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("""
                SELECT COUNT(u) > 0
                FROM User u
                JOIN u.userAuthProviders uap
                JOIN uap.authProvider
                WHERE uap.authProviderEmail = :email AND uap.authProvider = :authProvider
            """)
    boolean existsByEmail(@Param("email") String email, @Param("authProvider")AuthProvider authProvider);
}
