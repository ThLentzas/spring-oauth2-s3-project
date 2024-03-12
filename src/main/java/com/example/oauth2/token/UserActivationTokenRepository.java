package com.example.oauth2.token;

import com.example.oauth2.entity.User;
import com.example.oauth2.entity.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface UserActivationTokenRepository extends JpaRepository<UserActivationToken, Long> {
    @Query("""
                SELECT uvt
                FROM UserActivationToken uvt
                JOIN FETCH uvt.user
                WHERE uvt.tokenValue = :tokenValue
            """)
    Optional<UserActivationToken> findByTokenValue(@Param("tokenValue") String tokenValue);

    @Query("""
                DELETE
                FROM UserActivationToken uvt
                WHERE uvt.user = :user
            """)
    void deleteAllTokensByUser(@Param("user") User user);
}
