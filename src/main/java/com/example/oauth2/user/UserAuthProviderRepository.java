package com.example.oauth2.user;

import com.example.oauth2.authprovider.AuthProviderType;
import com.example.oauth2.entity.UserAuthProvider;
import com.example.oauth2.entity.key.UserAuthProviderKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, UserAuthProviderKey> {
    @Query("""
                SELECT uap
                FROM UserAuthProvider  uap
                JOIN FETCH uap.user
                WHERE uap.authProviderEmail = :email AND uap.authProvider.authProviderType = :authProviderType
            """)
    Optional<UserAuthProvider> findByEmailAndProvider(@Param("email") String email,
                                                      @Param("authProviderType") AuthProviderType authProviderType);
}
