package lk.ijse.back_end.controller;

import jakarta.validation.Valid;
import lk.ijse.back_end.dto.*;

import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.JwtUtil;
import lk.ijse.back_end.util.UserType;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
//    private final FileStorageService fileStorageService;

    @Autowired
    public SellerController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;

    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerSeller(
            @Valid @ModelAttribute SellerDTO sellerDTO,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            // Validate required fields
            if (sellerDTO.getEmail() == null || sellerDTO.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Email and password are required", null));
            }

            // Handle file upload
            if (profileImage != null && !profileImage.isEmpty()) {
//                String fileName = fileStorageService.storeFile(profileImage);
//                sellerDTO.setProfileImage("/uploads/" + fileName);
            }

            // Set server-side values
            sellerDTO.setCreatedAt(LocalDateTime.now());
            sellerDTO.setType(UserType.SELLER);
            sellerDTO.setSkillIds(Collections.emptyList());
            sellerDTO.setServiceIds(Collections.emptyList());
            sellerDTO.setRatingIds(Collections.emptyList());
            sellerDTO.setQualifications(Collections.emptyList());

            // Save user and get result
            int result = userService.saveUser(sellerDTO);

            switch (result) {
                case VarList.Created -> {
                    // Create UserDTO for token generation
                    UserDTO authUserDTO = new UserDTO();
                    authUserDTO.setEmail(sellerDTO.getEmail());
                    authUserDTO.setType(sellerDTO.getType());

                    String token = jwtUtil.generateToken((UserDetails) authUserDTO);
                    AuthResponseDTO authResponse = new AuthResponseDTO();
                    authResponse.setEmail(sellerDTO.getEmail());
                    authResponse.setToken(token);
                    authResponse.setUserType(sellerDTO.getType());
                    authResponse.setExpiresAt(
                            jwtUtil.getExpirationDateFromToken(token)
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime()
                    );

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Seller registration successful", authResponse));
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
}