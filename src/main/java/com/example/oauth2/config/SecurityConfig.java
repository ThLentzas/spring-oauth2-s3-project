package com.example.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import com.example.oauth2.security.FormLoginSuccessHandler;
import com.example.oauth2.user.UserService;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
class SecurityConfig {
    private final UserService userService;
    /*
        If we had both formLogin(), httpBasic() and oauth2Login() and a request comes in with username/password and
        username:password in the Authorization header, there is priority based on how we declare things. So 1st we
        would perform username and password authentication
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.GET, "/register").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/password_reset/confirm/**").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/api/v1/user/verify/**").permitAll();
                    authorize.requestMatchers("/api/v1/auth/**").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository());
                    csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler());
                })
                //Explain with defaults()
                .formLogin(formLoginConfigurer -> formLoginConfigurer.successHandler(new FormLoginSuccessHandler(userService)))
                //it uses Open ID Connect under the hood for Google and oauth2 for GitHub
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