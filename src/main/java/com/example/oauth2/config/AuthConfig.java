package com.example.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
class AuthConfig {
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService () {
        return username -> userRepository.findByEmail(username)
                .map(UsernamePasswordUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username or password is incorrect"));
    }
}