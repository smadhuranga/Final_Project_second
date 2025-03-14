package lk.ijse.back_end.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String email;
    private String password;
}