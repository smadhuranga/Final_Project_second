package lk.ijse.back_end.controller;


import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    @Autowired
    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                          UserServiceImpl userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Unauthorized, "Authentication Failed", null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        UserDTO userDTO = userService.searchUser(authRequest.getEmail());
        String token = jwtUtil.generateToken(userDTO);

        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setToken(token);
        authResponse.setUserType(userDTO.getType().name());

        return new ResponseEntity<>(
                new ResponseDTO(VarList.OK, "Success", authResponse),
                HttpStatus.OK
        );
    }
}