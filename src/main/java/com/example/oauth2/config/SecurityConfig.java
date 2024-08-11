package com.example.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import com.example.oauth2.security.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig {
    /*
        If we had both formLogin(), httpBasic() and oauth2Login() and a request comes in with username/password and
        username:password in the Authorization header, there is priority based on how we declare things. So 1st we
        would perform username and password authentication
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.GET, "/register").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/account_activation").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/css/**").permitAll();
                    authorize.requestMatchers(HttpMethod.GET, "/password_reset/**").permitAll();
                    //It's permitAll() because its the endpoint that will be called from the user clicking the link on their email
                    authorize.requestMatchers(HttpMethod.GET, "/api/v1/user/verify/**").permitAll();
                    authorize.requestMatchers("/api/v1/auth/**").permitAll();
                    authorize.anyRequest().authenticated();
                })
                // Explicit saving: https://docs.spring.io/spring-security/reference/6.0/migration/servlet/session-management.html#_require_explicit_saving_of_securitycontextrepository
                .securityContext(securityContext -> securityContext.requireExplicitSave(true))
                .csrf(csrf -> {
                    csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository());
                    csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler());
                })

                //https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
                .formLogin(formLoginConfigurer -> {
                    formLoginConfigurer.loginPage("/login").permitAll();
                    // successForwardUrl() makes a POST request to the argument passed, we want to navigate the user to
                    // "/profile" with a GET request
                    formLoginConfigurer.defaultSuccessUrl("/profile");
                })
                //it uses Open ID Connect under the hood for Google and oauth2 for GitHub
                .oauth2Login(oAuth2LoginConfigurer -> {
                    oAuth2LoginConfigurer.loginPage("/login");
                    oAuth2LoginConfigurer.successHandler(new OAuth2SuccessHandler());
                });
        return http.build();
    }
}