package com.example.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(OAuth2AuthenticationToken token, Authentication authentication) {
        System.out.println(authentication);

        return "Hello" + token.getName();
    }
}
