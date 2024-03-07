package com.example.oauth2.user;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        boolean activated = this.userService.verifyUser(token);

        if(activated) {
            return "redirect:/login";
        }
        //toDO: redirect to error page

        return null;
    }

    //PutMapping can exist without request body. This also might the wrong HTTP verb to use
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/account/activate")
    ResponseEntity<Void> activateAccount(@AuthenticationPrincipal UsernamePasswordUser user) {
       this.userService.activateUserAccount(user);

       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
