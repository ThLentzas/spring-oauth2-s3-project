package com.example.oauth2.token;

import com.example.oauth2.entity.User;
import com.example.oauth2.entity.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface UserActivationTokenRepository extends JpaRepository<UserActivationToken, UUID> {
    @Query("""
                SELECT uvt
                FROM UserActivationToken uvt
                JOIN FETCH uvt.user
                WHERE uvt.tokenValue = :tokenValue
            """)
    Optional<UserActivationToken> findByTokenValue(@Param("tokenValue") String tokenValue);

    @Modifying
    @Query("""
                DELETE
                FROM UserActivationToken uvt
                WHERE uvt.user = :user
            """)
    void deleteTokensByUser(@Param("user") User user);
}
