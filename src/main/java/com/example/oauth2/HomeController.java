package com.example.oauth2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
class HomeController {

    @GetMapping("/home")
    String home(Principal principal) {
        return principal.getName();
    }
}
