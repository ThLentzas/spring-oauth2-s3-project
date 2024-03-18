package com.example.oauth2.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.oauth2.entity.AuthProvider;
import com.example.oauth2.entity.User;

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

    /*
        The user initially have no social accounts tied to their profile, so we use LEFT JOIN FETCH to fetch the user
        even if no accounts where found.
     */
    @Query("""
                SELECT u
                FROM User u
                JOIN FETCH u.userAuthProviders uap
                JOIN FETCH uap.authProvider
                LEFT JOIN FETCH u.socialAccounts
                WHERE u.id = :id
            """)
    Optional<User> findByIdFetchingSocialAccounts(@Param("id") Long id);

    @Query("""
                SELECT COUNT(u) > 0
                FROM User u
                JOIN u.userAuthProviders uap
                JOIN uap.authProvider
                WHERE uap.authProviderEmail = :email AND uap.authProvider = :authProvider
            """)
    boolean existsByEmail(@Param("email") String email, @Param("authProvider") AuthProvider authProvider);
}
