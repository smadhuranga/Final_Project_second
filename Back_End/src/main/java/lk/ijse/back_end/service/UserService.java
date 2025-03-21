package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {
    UserDTO searchUser(String username);
    int saveUser(UserDTO userDTO);
    String storeProfileImage(String email, MultipartFile file);
    int changePassword(String email, String currentPassword, String newPassword);

    public UserDTO findUserByEmail(String email);
    public int updateUser(UserDTO userDTO);
}


