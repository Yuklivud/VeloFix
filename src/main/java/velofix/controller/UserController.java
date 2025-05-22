package velofix.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import velofix.dto.UserNameDto;
import velofix.model.entity.User;
import velofix.repository.UserRepository;
import velofix.service.AuthService;

import java.util.Optional;
import java.util.UUID;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, AuthService authService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/personal")
    public String showPersonalDataForm(Model model,
                                       @CookieValue(value = "sessiontoken", required = false) String token) {
        Optional<UUID> userIdOpt = authService.getUserIdFromSession(token);
        if (token == null || userIdOpt.isEmpty()) {
            return "redirect:/signIn";
        }
        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        UUID userId = userIdOpt.get();
        User user = userRepository.findById(userId).orElseThrow();

        model.addAttribute("userNameDto", new UserNameDto(user));
        return "personal";
    }


    @PostMapping("/personal")
    public String updatePersonalData(@Valid @ModelAttribute("userNameDto") UserNameDto dto,
                                     BindingResult bindingResult,
                                     @CookieValue(value = "sessiontoken", required = false) String token,
                                     Model model) {
        if (token == null || authService.getUserIdFromSession(token).isEmpty()) {
            return "redirect:/signIn";
        }

        UUID userId = authService.getUserIdFromSession(token).get();
        User user = userRepository.findById(userId).orElseThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Validation error");
            return "personal";
        }

        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);

        model.addAttribute("success", "Changes saved successfully!");
        return "personal";
    }
}
