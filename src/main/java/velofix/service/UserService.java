package velofix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import velofix.model.entity.User;
import velofix.model.enums.UserRole;
import velofix.model.enums.UserStatus;
import velofix.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findUsersByFilter(String fullName, UserRole role) {
        if ((fullName == null || fullName.isBlank()) && role == null) {
            return userRepository.findAll();
        }
        return userRepository.findByFullNameAndRole(
                fullName != null && !fullName.isBlank() ? fullName.trim() : null,
                role
        );
    }

    public void updateUserRoleAndStatus(UUID id, UserRole role, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        user.setStatus(status);
        userRepository.save(user);
    }
}
