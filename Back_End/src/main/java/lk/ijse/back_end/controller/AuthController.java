package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.*;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.JwtUtil;
import lk.ijse.back_end.util.UserType;
import lk.ijse.back_end.util.VarList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public AuthController(JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          UserServiceImpl userServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody CustomerDTO authRequest) {
        try {
            // Validate input
            if (authRequest == null || authRequest.getEmail() == null || authRequest.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO<>(VarList.Bad_Request, "Email and password are required", null));
            }

            System.out.println("Login attempt for: " + authRequest.getEmail());
            System.out.println("Raw password received: " + authRequest.getPassword());

            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            // Fetch UserDTO using UserServiceImpl
            UserDTO userDTO = userServiceImpl.searchUser(authRequest.getEmail());
            if (userDTO == null) {
                throw new UsernameNotFoundException("User not found after authentication");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(userDTO);

            // Build response DTO
            AuthResponseDTO authResponse = new AuthResponseDTO();
            authResponse.setToken(token);
            authResponse.setEmail(userDTO.getEmail());
            authResponse.setUserType(UserType.valueOf(userDTO.getType().name()));
            authResponse.setExpiresAt(
                    jwtUtil.getExpirationDateFromToken(token)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );

            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Login successful", authResponse)
            );

        } catch (BadCredentialsException e) {
            System.err.println("Authentication error: Invalid credentials for " + authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(VarList.Unauthorized, "Invalid email or password", null));
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Authentication failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ResponseDTO<AuthValidationResponse>> validateToken(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(VarList.Unauthorized, "Not authenticated", null));
        }

        try {
            UserDTO userDTO = userServiceImpl.searchUser(userDetails.getUsername());

            AuthValidationResponse response = new AuthValidationResponse();
            response.setEmail(userDTO.getEmail());
            response.setUserType(userDTO.getType());
            response.setName(userDTO.getName());
            response.setProfileImage(userDTO.getProfileImage());

            return ResponseEntity.ok()
                    .body(new ResponseDTO<>(VarList.OK, "Valid session", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Error validating token", null));
        }
    }

}