package velofix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import velofix.service.AuthService;

@Controller
public class MainPageController {

    private AuthService authService;

    @Autowired
    public MainPageController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping({"/", "/main-page", "main"})
    public String showMainPage(Model model, @CookieValue(value = "sessiontoken", required = false) String token) {
        if (token != null) {
            authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        }
        return "main-page";
    }
}
