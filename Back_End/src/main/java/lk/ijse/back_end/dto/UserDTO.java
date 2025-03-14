package lk.ijse.back_end.dto;

import lk.ijse.back_end.util.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String profileImage;
    private UserType type;
    private LocalDateTime createdAt;
}