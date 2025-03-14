package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDTO searchUser(String username);
    int saveUser(UserDTO userDTO);
}


