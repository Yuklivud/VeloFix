package velofix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import velofix.model.entity.User;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<UUID> getUserIdFromSession(String token) {
        String sql = "SELECT userid FROM usersession WHERE sessiontoken = ? AND expiresat > now()";
        try {
            UUID userId = jdbcTemplate.queryForObject(sql, new Object[]{token}, UUID.class);
            return Optional.of(userId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public Optional<User> getUserFromSession(String token) {
        String sql = """
                SELECT u.*
                FROM usersession s
                JOIN users u ON s.userid = u.id
                WHERE s.sessiontoken = ? AND s.expiresat > now()
                """;
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{token}, new BeanPropertyRowMapper<>(User.class));
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

