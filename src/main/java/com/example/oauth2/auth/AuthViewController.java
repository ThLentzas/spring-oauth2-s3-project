package com.example.oauth2.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.oauth2.token.dto.PasswordResetConfirmationRequest;
import com.example.oauth2.auth.usernamepassword.dto.RegisterRequest;
import com.example.oauth2.token.dto.PasswordResetRequest;

@Controller
class AuthViewController {

    /*
        1. Spring uses a ViewResolver to map the logical view name to an actual view template. The ViewResolver will
           map "login" to a file named login.html in a directory of templates (like src/main/resources/templates)
        2. The rendered HTML from the view template is then sent back as the HTTP response body. This response is
           typically of content-type text/html, making it suitable for display in web browsers.
        3. The client (a web browser) receives the full HTML content as the response body. It then renders this HTML
           content, displaying the formatted view to the user.

           When we are doing  SSR and the server returns a view, what basically happens is that in the response body,
           there is just a long string, which is the html page and  the content-type header is set to text/html? Then
           the browser reads the response and renders the page? => Answer: Yes
     */
    @GetMapping("/login")
    String loginView() {
        return "login";
    }

    @GetMapping("/register")
    String registrationView(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());

        return "register";
    }

    @GetMapping("/account_activation")
    String accountActivationView() {
        return "account_activation";
    }

    @GetMapping("/password_reset")
    String passwordResetView(Model model) {
        model.addAttribute("passwordResetRequest", new PasswordResetRequest());

        return "password_reset";
    }

    @GetMapping("/password_reset/confirm")
    String passwordResetConfirmationView(Model model) {
        model.addAttribute("passwordResetConfirmationRequest", new PasswordResetConfirmationRequest());

        return "password_reset_confirm";
    }
}
