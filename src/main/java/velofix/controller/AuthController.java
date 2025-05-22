package velofix.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import velofix.dto.UserRegistrationDto;
import velofix.model.entity.User;
import velofix.model.enums.UserRole;
import velofix.model.enums.UserStatus;
import velofix.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository,
                          JdbcTemplate jdbcTemplate,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "signUp";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("userDto") @Valid UserRegistrationDto dto,
                               BindingResult bindingResult,
                               Model model) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "Email already registered");
        }

        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            bindingResult.rejectValue("phone", "error.user", "Phone number already registered");
        }

        if (bindingResult.hasErrors()) {
            return "signUp";
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName(dto.getFullname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.CLIENT);
        user.setSignupDate(LocalDate.now());
        user.setLastLoginAt(null);
        userRepository.save(user);

        return "signIn";
    }
}