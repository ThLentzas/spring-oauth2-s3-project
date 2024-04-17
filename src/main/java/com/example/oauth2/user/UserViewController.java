package com.example.oauth2.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.oauth2.auth.oauth2.SocialLoginUser;
import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.user.dto.UserProfile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
class UserViewController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('USER', 'VERIFIED')")
    @GetMapping("/profile")
    String profile(Model model, Authentication authentication) {
        Long userId = authentication.getPrincipal() instanceof UsernamePasswordUser usernamePasswordUser
                ? usernamePasswordUser.user().getId()
                : ((SocialLoginUser) authentication.getPrincipal()).user().getId();
        UserProfile userProfile = this.userService.findByIdFetchingSocialAccounts(userId, authentication);
        model.addAttribute("userProfile", userProfile);

        return "profile";
    }
}
