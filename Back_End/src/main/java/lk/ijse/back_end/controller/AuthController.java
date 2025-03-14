package lk.ijse.back_end.controller;



import lk.ijse.back_end.dto.AuthRequestDTO;
import lk.ijse.back_end.dto.AuthResponseDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.JwtUtil;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    @Autowired
    public AuthController(JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          UserServiceImpl userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthResponseDTO>> login(@RequestBody AuthRequestDTO authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            UserDTO userDTO = userService.searchUser(authRequest.getEmail());
            String token = jwtUtil.generateToken(userDTO);

            AuthResponseDTO authResponse = new AuthResponseDTO();
            authResponse.setToken(token);
            authResponse.setEmail(userDTO.getEmail());
            authResponse.setUserType(userDTO.getType());

            // Fixed date conversion
            authResponse.setExpiresAt(
                    jwtUtil.getExpirationDateFromToken(token)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );

            return ResponseEntity.ok(
                    new ResponseDTO<>(
                            VarList.OK,
                            "Login successful",
                            authResponse
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(
                            VarList.Unauthorized,
                            "Invalid credentials",
                            null
                    ));
        }
    }
}