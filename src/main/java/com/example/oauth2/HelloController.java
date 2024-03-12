package com.example.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(Authentication authentication, HttpServletRequest request) {
        System.out.println(authentication);
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        return csrfToken.getToken();
    }
}
