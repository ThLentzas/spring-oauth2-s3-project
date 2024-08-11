package com.example.oauth2.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.user.dto.UserProfileUpdateRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    /*
        If the validation of the token in the activation link was successful the user is redirected to the login page
        to log in to their new account, otherwise the token was not valid and the user is redirected to an
        error page. It's permitAll() because its the endpoint that will be called from the user clicking the link on
        their email
     */
    @GetMapping("/verify")
    String verifyUser(@RequestParam(name = "token", defaultValue = "") String token) {
        boolean activated = this.userService.verifyUser(token);
        if(activated) {
            return "redirect:/login";
        }

        return "redirect:/error";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/account/activate")
    String activateAccount(@AuthenticationPrincipal UsernamePasswordUser user) {
       this.userService.activateUserAccount(user);

       return "redirect:/account_activation";
    }

    @PreAuthorize("hasAnyRole('VERIFIED')")
    @GetMapping("/account/link")
    String linkAccount() {
        return "redirect:/login";
    }

    @PreAuthorize("hasRole('VERIFIED')")
    @PostMapping(value = "/profile/edit", consumes = "multipart/form-data")
    String updateProfile(@ModelAttribute("userProfileUpdateRequest") UserProfileUpdateRequest userProfileUpdateRequest,
                         Authentication authentication) {
        this.userService.updateUserProfile(authentication, userProfileUpdateRequest);

        return "redirect:/profile";
    }
}
