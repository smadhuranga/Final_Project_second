package lk.ijse.back_end.dto;

import lk.ijse.back_end.util.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String address;

    private String profileImage;

    private UserType type;

    private LocalDateTime createdAt;

// constructors, getters, setters


}