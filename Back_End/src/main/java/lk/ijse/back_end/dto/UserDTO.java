package lk.ijse.back_end.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lk.ijse.back_end.util.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "User type is mandatory")
    private UserType type;

    private String phone;
    private String address;
    private String profileImage;
    private LocalDateTime createdAt;  // Change from ZonedDateTime


    public UserDTO(String name, String email, String password, UserType type, String phone, String address, String profileImage) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.phone = phone;
        this.address = address;
        this.profileImage = profileImage;
    }



}