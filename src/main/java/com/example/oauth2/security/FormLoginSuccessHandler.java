package com.example.oauth2.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

/*
    When the user is authenticated via the form login we have to update the lastSignedInAt property of that user
 */
@RequiredArgsConstructor
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UsernamePasswordUser usernamePasswordUser = (UsernamePasswordUser) authentication.getPrincipal();
        this.userService.updateLastSignedInAt(usernamePasswordUser.user());

        response.sendRedirect("/hello");
    }
}
