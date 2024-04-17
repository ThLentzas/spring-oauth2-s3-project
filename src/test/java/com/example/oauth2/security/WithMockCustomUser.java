package com.example.oauth2.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

/*
    We need to set the @Retention to RUNTIME, because by default it is SOURCE and our annotation will not be visible
    to be intercepted via reflection at apps execution

    https://www.youtube.com/watch?v=onD_fyhy58o&list=PLEocw3gLFc8X_a8hGWGaBnSkPFJmbb8QP&index=39

    @WithSecurityContext(factory = CustomSecurityContextFactory.class) Whenever a test sees our custom annotation we
    specify which security context to be used.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "user";
    String password() default "password";
    String[] roles();
    /*
        Why we need socialLogin?
        In our application the authentication object can be either UsernamePasswordAuthenticationToken or
        OAuth2AuthenticationToken. In the 1st case the principal is UsernamePasswordUser because it implements
        UserDetails and in the 2nd case is SocialLoginUser because it implements OidcUser that extends OAuth2User.

        Given the following endpoint:
                @PreAuthorize("hasRole('USER')")
                @GetMapping("/account/activate")
                String activateAccount(@AuthenticationPrincipal UsernamePasswordUser user) {
                   this.userService.activateUserAccount(user);

                   return "redirect:/account_activation";
                }
        Using the default @WithMockUser the UsernamePasswordUser user will be null, because the principal of the default
        authentication object provided by Spring is of type UserDetails and our tests will fail. Following the same
        logic for an endpoint @AuthenticationPrincipal SocialLoginUser user will be null. When we create our
        authentication object we need to know the type of principal.
     */
    boolean socialLogin();
}