package com.example.oauth2.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.oauth2.config.SecurityConfig;
import com.example.oauth2.user.UserService;

@WebMvcTest(AuthViewController.class)
@Import(SecurityConfig.class)
class AuthViewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    void shouldReturnLoginView() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpectAll(
                        status().isOk(),
                        view().name("login")
                );
    }

    /*
        What we return from the controller is the html page which is a long string so, when we call containsString(),
        we check if in the html page there is the text that we specify as argument
     */
    @Test
    void shouldReturnLoginViewWithPasswordResetGenericResponse() throws Exception {
        this.mockMvc.perform(get("/login")
                        .flashAttr("passwordResetGenericResponse", true))
                .andExpectAll(
                        status().isOk(),
                        view().name("login"),
                        content().string(containsString("If your email is registered, you will receive a password reset link."))
                );
    }

    /*
        What we return from the controller is the html page which is a long string so, when we call containsString(),
        we check if in the html page there is the text that we specify as argument
     */
    @Test
    void shouldReturnLoginViewWithPasswordResetSuccessMessage() throws Exception {
        this.mockMvc.perform(get("/login")
                        .flashAttr("passwordResetSuccess", true))
                .andExpectAll(
                        status().isOk(),
                        view().name("login"),
                        content().string(containsString("Password has been reset successfully. You can now log in."))
                );
    }

    @Test
    void shouldReturnRegisterView() throws Exception {
        this.mockMvc.perform(get("/register"))
                .andExpectAll(
                        status().isOk(),
                        view().name("register"),
                        model().attributeExists("registerRequest")
                );
    }

    @Test
    void shouldReturnAccountActivationView() throws Exception {
        this.mockMvc.perform(get("/account_activation"))
                .andExpectAll(
                        status().isOk(),
                        view().name("account_activation")
                );
    }

    @Test
    void shouldReturnPasswordResetView() throws Exception {
        this.mockMvc.perform(get("/password_reset"))
                .andExpectAll(
                        status().isOk(),
                        view().name("password_reset"),
                        model().attributeExists("passwordResetRequest")
                );
    }

    @Test
    void shouldReturnPasswordResetConfirmationView() throws Exception {
        this.mockMvc.perform(get("/password_reset/confirm"))
                .andExpectAll(
                        status().isOk(),
                        view().name("password_reset_confirm"),
                        model().attributeExists("passwordResetConfirmationRequest")
                );
    }
}
