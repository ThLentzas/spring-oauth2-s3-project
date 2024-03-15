package com.example.oauth2.token;

import com.example.oauth2.entity.PasswordResetToken;
import com.example.oauth2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    //Queries that modify the DB(write) like DELETE in our case needs to add @Modifying
    @Modifying
    @Query("""
                DELETE
                FROM PasswordResetToken prt
                WHERE prt.user = :user
            """)
    void deleteTokensByUser(@Param("user") User user);

    @Query("""
                SELECT prt
                FROM PasswordResetToken prt
                JOIN FETCH prt.user
                WHERE prt.tokenValue = :tokenValue
            """)
    Optional<PasswordResetToken> findByTokenValue(@Param("tokenValue") String tokenValue);
}
