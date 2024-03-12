package com.example.oauth2.authprovider;

import com.example.oauth2.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    @Query("""
                SELECT ap
                FROM AuthProvider ap
                WHERE ap.authProviderType = :authProviderType
            """)
    Optional<AuthProvider> findByAuthProvider(@Param("authProviderType") AuthProviderType authProviderType);
}
