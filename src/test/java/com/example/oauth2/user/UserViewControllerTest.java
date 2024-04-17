package com.example.oauth2.user;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.example.oauth2.config.SecurityConfig;
import com.example.oauth2.security.WithMockCustomUser;
import com.example.oauth2.socialaccount.dto.SocialAccountDTO;
import com.example.oauth2.user.dto.UserProfile;

import java.util.Collections;
import java.util.List;

@WebMvcTest(UserViewController.class)
@Import(SecurityConfig.class)
class UserViewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    @WithMockCustomUser(roles = "USER", socialLogin = false)
    void shouldReturnUserProfileForNonVerifiedUser() throws Exception {
        var userProfile = new UserProfile();
        userProfile.setName("user");
        userProfile.setSocialAccounts(Collections.emptyList());
        userProfile.setEnabled(false);

        when(this.userService.findByIdFetchingSocialAccounts(
                any(Long.class),
                any(Authentication.class))).thenReturn(userProfile);

        this.mockMvc.perform(get("/profile"))
                .andExpectAll(
                        status().isOk(),
                        view().name("profile"),
                        model().attributeExists("userProfile"),
                        model().attribute("userProfile", hasProperty("name", is("user"))),
                        model().attribute("userProfile", hasProperty("enabled", is(false))),
                        model().attribute("userProfile", hasProperty("socialAccounts", hasSize(0))),
                        //For the false condition content().string(not(containsString("Activate account")))
                        content().string(containsString("Activate account"))
                );
    }

    @Test
    @WithMockCustomUser(roles = "VERIFIED", socialLogin = false)
    void shouldReturnUserProfileForVerifiedUser() throws Exception {
        var userProfile = new UserProfile();
        userProfile.setName("user");
        userProfile.setSocialAccounts(List.of(
                new SocialAccountDTO(1L, "in/user"),
                new SocialAccountDTO(2L, "github.com/user"))
        );
        userProfile.setEnabled(true);

        when(this.userService.findByIdFetchingSocialAccounts(
                any(Long.class),
                any(Authentication.class))).thenReturn(userProfile);

        this.mockMvc.perform(get("/profile"))
                .andExpectAll(
                        status().isOk(),
                        view().name("profile"),
                        model().attributeExists("userProfile"),
                        model().attribute("userProfile", hasProperty("name", is("user"))),
                        model().attribute("userProfile", hasProperty("enabled", is(true))),
                        model().attribute("userProfile", hasProperty("socialAccounts", hasSize(2))),
                        //For the false condition content().string(not(containsString("Link an account")))
                        content().string(containsString("Link an account"))
                );
    }
}