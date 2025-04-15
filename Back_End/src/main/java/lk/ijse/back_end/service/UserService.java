package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.SellerDTO;
import lk.ijse.back_end.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO searchUser(String username);
    int saveUser(UserDTO userDTO);
    String storeProfileImage(String email, MultipartFile file);
    int changePassword(String email, String currentPassword, String newPassword);

    public UserDTO findUserByEmail(String email);
    public int updateUser(UserDTO userDTO);

    List<UserDTO> getAllUsers();
    int deleteUser(Long id);
    int resetPassword(String email, String newPassword);
    Long getUserIdByEmail(String email);
    SellerDTO updateSellerProfile(String email, SellerDTO sellerDTO);

    int deleteUserByEmail(String email);
    // In UserService.java
    boolean userExists(Long userId);
}


