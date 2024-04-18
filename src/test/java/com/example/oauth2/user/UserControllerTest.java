package com.example.oauth2.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.oauth2.config.SecurityConfig;
import com.example.oauth2.auth.usernamepassword.UsernamePasswordUser;
import com.example.oauth2.security.WithMockCustomUser;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToLoginViewAfterSuccessfulUserVerification() throws Exception {
        String token = "token";

        when(this.userService.verifyUser(token)).thenReturn(Boolean.TRUE);

        this.mockMvc.perform(get("/api/v1/user/verify?token={token}", token))
                .andExpectAll(
                        status().is3xxRedirection(),
                        // We can also use: redirectedUrl("/login")
                        header().string("Location", "/login")
                );
    }

    /*
        Since we are setting socialLogin = false the principal created in the custom security context is of type
        UsernamePasswordUser and when the endpoint gets called, the @AuthenticationPrincipal UsernamePasswordUser user
        passed is not null.

            @PreAuthorize("hasRole('USER')")
            @GetMapping("/account/activate")
            String activateAccount(@AuthenticationPrincipal UsernamePasswordUser user) {
               this.userService.activateUserAccount(user);

               return "redirect:/account_activation";
            }
     */
    @Test
    @WithMockCustomUser(roles = "USER", socialLogin = false)
    void shouldActivateUserAccount() throws Exception {
        doNothing().when(this.userService).activateUserAccount(any(UsernamePasswordUser.class));

        this.mockMvc.perform(get("/api/v1/user/account/activate"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        // We can also use: redirectedUrl("/account_activation")
                        header().string("Location", "/account_activation")
                );

        verify(this.userService, times(1)).activateUserAccount(any(UsernamePasswordUser.class));
    }
}
