//package lk.ijse.back_end.service.impl;
//
//import lk.ijse.back_end.dto.UserDTO;
//import lk.ijse.back_end.entity.User;
//import lk.ijse.back_end.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//public class CustomrServiceImpl implements UserService {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Validate password exists
//        if (user.getPassword() == null || user.getPassword().isEmpty()) {
//            throw new AuthenticationCredentialsNotFoundException("No password set");
//        }
//
//        return new UserDTO(
//                user.getEmail(),
//                user.getPassword(), // Must return encoded password
//                user.getUserType().name()
//        );
//    }
//}
