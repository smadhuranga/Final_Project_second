package lk.ijse.back_end.dto;

import lk.ijse.back_end.util.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthResponseDTO {
    private String token;
    private String email;
    private UserType userType;
    private LocalDateTime expiresAt;
}