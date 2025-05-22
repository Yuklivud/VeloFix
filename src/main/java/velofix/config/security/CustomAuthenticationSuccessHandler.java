package velofix.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import velofix.model.entity.User;
import velofix.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        UUID sessionId = UUID.randomUUID();
        String sessionToken = UUID.randomUUID().toString();

        jdbcTemplate.update("INSERT INTO usersession (sessionid, userid, sessiontoken, createdat, expiresat) " +
                        "VALUES (?, ?, ?, now(), now() + interval '7 days')",
                sessionId, user.getId(), sessionToken);

        ResponseCookie cookie = ResponseCookie.from("sessiontoken", sessionToken)
                .path("/")
                .httpOnly(true)
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect("/create");
    }
}

