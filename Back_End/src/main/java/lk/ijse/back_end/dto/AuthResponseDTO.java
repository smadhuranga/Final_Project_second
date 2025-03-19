package lk.ijse.back_end.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lk.ijse.back_end.util.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponseDTO {
    private String token;
    private String email;
    private UserType userType;
    private LocalDateTime expiresAt;

    public AuthResponseDTO(@Email(message = "Invalid email format") @NotBlank(message = "Email is mandatory") String email, String token) {
        this.email = email;
        this.token = token;
    }


}