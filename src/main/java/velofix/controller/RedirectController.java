package velofix.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/signIn")
    public String signIn(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/create";
        }
        return "signIn";
    }

}

