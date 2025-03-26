package lk.ijse.back_end.controller;


import jakarta.validation.Valid;
import jdk.jfr.ContentType;
import lk.ijse.back_end.dto.AuthResponseDTO;
import lk.ijse.back_end.dto.CustomerDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.JwtUtil;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin

public class CustomerController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CustomerController(@Qualifier("userServiceImpl") UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(path ="/register" , consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        try {
            // Log incoming request for debugging
            System.out.println("Received customerDTO: " + customerDTO);

            // Validate required fields
            if (customerDTO.getEmail() == null || customerDTO.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Email and password are required", null));
            }

            // Set server-side values
            customerDTO.setCreatedAt(LocalDateTime.now());
            customerDTO.setOrderIds(Collections.emptyList());

            // Save user and get result
            int result = userService.saveUser(customerDTO);
            switch (result) {
                case VarList.Created -> {
                    // Create UserDTO for token generation (with encoded password)
                    UserDTO userDTO = new UserDTO();
                    userDTO.setEmail(customerDTO.getEmail());
                    userDTO.setPassword(customerDTO.getPassword()); // Already encoded in service
                    userDTO.setType(customerDTO.getType()); // Ensure type is set if required by JWT

                    String token = jwtUtil.generateToken(userDTO);
                    AuthResponseDTO authDTO = new AuthResponseDTO();
                    authDTO.setEmail(customerDTO.getEmail());
                    authDTO.setToken(token);
                    authDTO.setUserType(customerDTO.getType()); // Assuming CustomerDTO has type
                    authDTO.setExpiresAt(
                            jwtUtil.getExpirationDateFromToken(token)
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                    );

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Registration successful", authDTO));
                }
                case VarList.Conflict -> {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ResponseDTO(VarList.Conflict, "Email already exists", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseDTO(VarList.Bad_Request, "Invalid request", null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Registration failed: " + e.getMessage(), null));
        }
    }

// Add these to your CustomerController.java

    @GetMapping("/me")
    public ResponseEntity<ResponseDTO> getCurrentCustomer(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            CustomerDTO customer = (CustomerDTO) userService.findUserByEmail(email);
            System.out.println(customer.getProfileImage());
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Success", customer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error retrieving profile", null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateCustomer(
            @Valid @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            CustomerDTO existingCustomer = (CustomerDTO) userService.findUserByEmail(email);

            // Update only allowed fields
            if (request.containsKey("name")) {
                existingCustomer.setName(request.get("name"));
            }
            if (request.containsKey("email")) {
                existingCustomer.setEmail(request.get("email"));
            }
            if (request.containsKey("phone")) {
                existingCustomer.setPhone(request.get("phone"));
            }
            if (request.containsKey("address")) {
                existingCustomer.setAddress(request.get("address"));
            }

            int result = userService.updateUser(existingCustomer);
            if (result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Profile updated", existingCustomer));
            }
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(VarList.Bad_Request, "Update failed", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PostMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO(VarList.Bad_Request, "File cannot be empty", null));
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO(VarList.Bad_Request, "Only image files are allowed", null));
            }

            String email = userDetails.getUsername();
            String imageUrl = userService.storeProfileImage(email, file);
            System.out.println("image url is "+imageUrl);

            // Update user's profile image URL in database
            CustomerDTO customer = (CustomerDTO) userService.findUserByEmail(email);
            customer.setProfileImage(userService.storeProfileImage(email, file));
            userService.updateUser(customer);

            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Image uploaded successfully", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error processing image: " + e.getMessage(), null));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseDTO> changePassword(
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            int result = userService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());

            switch (result) {
                case VarList.OK:
                    return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Password changed", null));
                case VarList.Unauthorized:
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ResponseDTO(VarList.Unauthorized, "Current password incorrect", null));
                default:
                    return ResponseEntity.badRequest()
                            .body(new ResponseDTO(VarList.Bad_Request, "Password change failed", null));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Add this DTO inside CustomerController.java
    private static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;

        // Getters and setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }


}