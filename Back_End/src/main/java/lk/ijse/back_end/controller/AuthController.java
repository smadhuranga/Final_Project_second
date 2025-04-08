package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.*;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.*;
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
import java.util.Map;

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

    private final OtpUtil otpUtil;
    private final EmailUtil emailUtil;
    private final UserService userService;

    @Autowired
    public AuthController(JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          UserServiceImpl userServiceImpl,OtpUtil otpUtil, EmailUtil emailUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userServiceImpl = userServiceImpl;
        this.otpUtil = otpUtil;
        this.emailUtil = emailUtil;
        this.userService = userService;

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


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            UserDTO userDTO = userServiceImpl.searchUser(authRequest.getEmail());
            if (userDTO == null) {
                throw new UsernameNotFoundException("User not found after authentication");
            }

            String token = jwtUtil.generateToken(userDTO);

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


    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO> sendPasswordResetOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO(VarList.Bad_Request, "Email is required", null));
            }

            UserDTO user = userService.searchUser(email);
            if (user == null || !user.getEmail().equals(email)) {
                return ResponseEntity.ok()
                        .body(new ResponseDTO(VarList.OK, "If the email exists, OTP has been sent", null));
            }

            String otp = otpUtil.generateOtp(email);
            emailUtil.sendOtpEmail(email, otp, "password reset");

            return ResponseEntity.ok()
                    .body(new ResponseDTO(VarList.OK, "OTP has been sent to your email", null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error processing request", null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            // Validate OTP first
            if (!otpUtil.validateOtp(request.getEmail(), request.getOtp())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO(VarList.Unauthorized, "Invalid or expired OTP", null));
            }

            int result = userService.resetPassword(request.getEmail(), request.getNewPassword());
            if (result == VarList.OK) {
                otpUtil.removeOtp(request.getEmail());
                return ResponseEntity.ok()
                        .body(new ResponseDTO(VarList.OK, "Password reset successful", null));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(result, "Password reset failed", null));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error resetting password: " + e.getMessage(), null));
        }
    }

    public static class PasswordResetRequest {
        private String email;
        private String otp;
        private String newPassword;

        // Add proper getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}