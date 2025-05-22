package velofix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import velofix.model.entity.User;
import velofix.model.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findById(UUID uuid);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByRole(UserRole role);
    List<User> findAllByRole(UserRole role);
    @Query("SELECT u FROM User u " +
            "WHERE (:fullName IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
            "AND (:role IS NULL OR u.role = :role)")
    List<User> findByFullNameAndRole(@Param("fullName") String fullName, @Param("role") UserRole role);

}