package com.example.oauth2.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oauth2.auth.usernamepassword.UsernamePasswordService;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.config.SecurityConfig;
import com.example.oauth2.token.PasswordResetTokenService;
import com.example.oauth2.user.UserService;
import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;
import com.example.oauth2.token.dto.PasswordResetRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
    REDIRECT URL IS IN THE LOCATION HEADER and when the response to a request is 3xx it tells the browser to make a GET
    request to the URL of the Location header. The Location response header indicates the URL to redirect a page to
 */
@WebMvcTest
@Import(SecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UsernamePasswordService usernamePasswordService;
    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @Test
    void shouldRedirectToAccountActivationViewAfterRegisteringUser() throws Exception {
        doNothing().when(this.usernamePasswordService).registerUser(
                any(RegisterRequest.class),
                any(HttpServletRequest.class),
                any(HttpServletResponse.class));

        this.mockMvc.perform(post("/api/v1/auth/register").with(csrf())
                        .param("name", "test")
                        .param("email", "test@example.com")
                        .param("password", "password"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        // We can also use: redirectedUrl("/account_activation"),
                        header().string("Location", "/account_activation")
                );

        verify(this.usernamePasswordService, times(1)).registerUser(
                any(RegisterRequest.class),
                any(HttpServletRequest.class),
                any(HttpServletResponse.class));
    }

    @Test
    void shouldRedirectToLoginViewAfterPasswordResetRequest() throws Exception {
        doNothing().when(this.passwordResetTokenService).createPasswordResetToken(
                any(PasswordResetRequest.class),
                any(Boolean.class));

        this.mockMvc.perform(post("/api/v1/auth/password_reset").with(csrf())
                        .param("email", "test@example.com"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        /*
                            https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/flash-attributes.html
                            checks that the flash attribute "passwordResetGenericResponse" is actually present in the
                            response after the controller method is executed. It is used for condition rendering
                         */
                        flash().attribute("passwordResetGenericResponse", true),
                        // We can also use: redirectedUrl("/login")
                        header().string("Location", "/login")
                );

        verify(this.passwordResetTokenService, times(1)).createPasswordResetToken(
                any(PasswordResetRequest.class),
                any(Boolean.class));
    }

    @Test
    void shouldRedirectToLoginViewAfterSuccessfulPasswordReset() throws Exception {
        String token = "token";

        when(this.passwordResetTokenService.resetPassword(
                eq(token),
                any(PasswordResetConfirmationRequest.class))).thenReturn(Boolean.TRUE);

        this.mockMvc.perform(post("/api/v1/auth/password_reset/confirm?token={token}", token).with(csrf())
                        .param("newPassword", "new password")
                        .param("confirmationPassword", "new password"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        /*
                            checks that the flash attribute "passwordResetGenericResponse" is actually present in the
                            response after the controller method is executed. It is used for condition rendering
                         */
                        flash().attribute("passwordResetSuccess", true),
                        // We can also use: redirectedUrl("/login")
                        header().string("Location", "/login")
                );

        verify(this.passwordResetTokenService, times(1)).resetPassword(eq(token),
                any(PasswordResetConfirmationRequest.class));
    }
}
