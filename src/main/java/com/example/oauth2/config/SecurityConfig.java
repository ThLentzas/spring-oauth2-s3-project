package com.example.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /*
        If we had both formLogin(), httpBasic() and oauth2Login() and a request comes in with username/password and
        username:password in the Authorization header, there is priority based on how we declare things. So 1st we
        would perform username and password authentication
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated())
                //Explain with defaults()
                .formLogin(AbstractHttpConfigurer::disable)
                //it uses Open ID Connect under the hood for Google and just oauth2 for GitHub
                .oauth2Login(withDefaults());
        return http.build();
    }
}
