package lk.ijse.back_end.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.ijse.back_end.util.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponseDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String token;

    @NotNull
    private UserType userType;

    @NotNull
    private LocalDateTime expiresAt;



}