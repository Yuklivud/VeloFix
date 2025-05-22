package velofix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velofix.dto.UserFilterDto;
import velofix.model.entity.User;
import velofix.model.enums.UserRole;
import velofix.model.enums.UserStatus;
import velofix.repository.UserRepository;
import velofix.service.AuthService;
import velofix.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {


    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserAdminController(UserRepository userRepository, UserService userService, AuthService authService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authService = authService;
    }


    @GetMapping({"", "/"})
    public String listUsers(@RequestParam(required = false) String fullName,
                            @RequestParam(required = false) UserRole role,
                            Model model,
                            @CookieValue(value = "sessiontoken", required = false) String token) {
        authService.getUserFromSession(token).ifPresent(user -> model.addAttribute("sessionUser", user));
        List<User> users = userService.findUsersByFilter(fullName, role);
        Optional<User> sessionUserOpt = authService.getUserFromSession(token);
        UserRole currentUserRole = sessionUserOpt.map(User::getRole).orElse(null);

        List<UserRole> availableRoles = Arrays.stream(UserRole.values())
                .filter(r -> {
                    if (UserRole.MANAGER.equals(currentUserRole)) {
                        return r != UserRole.MANAGER && r != UserRole.DIRECTOR;
                    }
                    if (UserRole.MECHANIC.equals(currentUserRole)) {
                        return false;
                    }
                    return true;
                })
                .toList();

        model.addAttribute("users", users);
        model.addAttribute("filter", new UserFilterDto(fullName, role));
        model.addAttribute("roles", availableRoles);
        model.addAttribute("statuses", UserStatus.values());
        model.addAttribute("currentUserRole", currentUserRole);
        model.addAttribute("managerRole", UserRole.MANAGER);
        model.addAttribute("directorRole", UserRole.DIRECTOR);
        model.addAttribute("mechanicRole", UserRole.MECHANIC);

        return "adminUsers";
    }


    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable UUID id,
                           @RequestParam UserRole role,
                           @RequestParam UserStatus status) {
        userService.updateUserRoleAndStatus(id, role, status);
        return "redirect:/admin/users";
    }

}
