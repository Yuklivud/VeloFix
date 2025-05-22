package velofix.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import velofix.model.entity.User;
import velofix.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SessionTokenAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public SessionTokenAuthFilter(UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("sessiontoken".equals(cookie.getName())) {
                        String sessionToken = cookie.getValue();

                        UUID userId = findUserIdByToken(sessionToken);
                        if (userId != null) {
                            Optional<User> optionalUser = userRepository.findById(userId);
                            if (optionalUser.isPresent()) {
                                User user = optionalUser.get();

                                List<GrantedAuthority> authorities = List.of(
                                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                                );

                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private UUID findUserIdByToken(String token) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT userid FROM usersession WHERE sessiontoken = ? AND expiresat > now()",
                    new Object[]{token},
                    UUID.class
            );
        } catch (Exception e) {
            return null;
        }
    }
}

