package lk.ijse.back_end.dto;

import lk.ijse.back_end.util.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthValidationResponse {
    private String email;
    private UserType userType;
    private String name;
    private String profileImage;
}