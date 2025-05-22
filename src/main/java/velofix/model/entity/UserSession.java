package velofix.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "usersession")
public class UserSession {
    @Id
    private UUID sessionid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(name = "sessiontoken", length = 255)
    private String sessionToken;

    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @Column(name = "expiresat")
    private LocalDateTime expiresAt;

    public UUID getSessionid() {
        return sessionid;
    }

    public void setSessionid(UUID sessionid) {
        this.sessionid = sessionid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
