package velofix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import velofix.model.entity.User;

public class UserNameDto {

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name must be less than 50 characters")
    private String middleName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\+?[0-9\\- ]{7,15}", message = "Invalid phone number")
    private String phone;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    public UserNameDto() {}

    public UserNameDto(User user) {
        String[] parts = user.getFullName() != null ? user.getFullName().trim().split(" ") : new String[0];
        this.lastName = parts.length > 0 ? parts[0] : "";
        this.firstName = parts.length > 1 ? parts[1] : "";
        this.middleName = parts.length > 2 ? parts[2] : "";
        this.phone = user.getPhone();
        this.password = ""; // порожній, щоб не передавати хеш
    }

    public String getFullName() {
        return String.join(" ",
                lastName != null ? lastName.trim() : "",
                firstName != null ? firstName.trim() : "",
                middleName != null ? middleName.trim() : "").trim();
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
