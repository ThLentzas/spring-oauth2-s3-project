package com.example.oauth2.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @GetMapping("/verify")
    String verifyUser(@RequestParam("token") String token) {
        this.userService.verifyUser(token);

        return "redirect:/login";
    }
}
