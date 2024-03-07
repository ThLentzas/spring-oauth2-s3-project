package com.example.oauth2.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oauth2.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    /*
        The query means if the user is found with a given email always fetch its auth providers. A user always has at
        least 1 provider.
     */
    @Query("""
                SELECT u
                FROM User u
                JOIN FETCH u.authUserProviders
                WHERE u.email = :email
            """)
    Optional<User> findByEmail(@Param("email") String email);

    @Query("""
                SELECT COUNT(u) > 0
                FROM User u
                WHERE u.email = :email
            """)
    boolean existsByEmail(@Param("email") String email);
}
