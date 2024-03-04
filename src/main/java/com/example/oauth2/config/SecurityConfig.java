package com.example.oauth2.config;

import com.example.oauth2.entity.AuthUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    /*
        If we had both formLogin(), httpBasic() and oauth2Login() and a request comes in with username/password and
        username:password in the Authorization header, there is priority based on how we declare things. So 1st we
        would perform username and password authentication
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/api/v1/auth/**").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository());
                    csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler());
                })
                //Explain with defaults()
                .formLogin(withDefaults())
                //it uses Open ID Connect under the hood for Google and just oauth2 for GitHub
                .oauth2Login(withDefaults());
        return http.build();
    }
}

/*
 .oauth2Login(login ->
                        login.successHandler((request, response, authentication) -> {
                            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
                            SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
                            handler.onAuthenticationSuccess(request, response, authentication);
                        })
                );
 */