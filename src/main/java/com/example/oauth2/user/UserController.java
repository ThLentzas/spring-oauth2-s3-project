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

    /*
        If the validation of the token in the activation link was successful the user is redirected to the login page
        to log in to their new account, otherwise the token was not valid and the user is redirected to an
        error page.
     */
    @GetMapping("/verify")
    String verifyUser(@RequestParam(name = "token", defaultValue = "") String token) {
        boolean activated = this.userService.activateUserAccount(token);

        if(activated) {
            return "redirect:/login";
        }
        //toDO: redirect to error page

        return null;
    }
}
