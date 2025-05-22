package velofix.dto;

import velofix.model.enums.UserRole;

public class UserFilterDto {
    private String fullName;
    private UserRole role;

    public UserFilterDto() {}

    public UserFilterDto(String fullName, UserRole role) {
        this.fullName = fullName;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
