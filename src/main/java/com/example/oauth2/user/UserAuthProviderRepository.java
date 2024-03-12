package com.example.oauth2.user;

import com.example.oauth2.entity.UserAuthProvider;
import com.example.oauth2.entity.key.UserAuthProviderKey;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, UserAuthProviderKey> {
}
