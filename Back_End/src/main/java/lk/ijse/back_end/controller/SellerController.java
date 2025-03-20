package lk.ijse.back_end.controller;

import jakarta.validation.Valid;
import lk.ijse.back_end.dto.*;
import lk.ijse.back_end.service.FileStorageService;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.JwtUtil;
import lk.ijse.back_end.util.UserType;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final FileStorageService fileStorageService;

    @Autowired
    public SellerController(UserService userService, JwtUtil jwtUtil, FileStorageService fileStorageService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerSeller(
            @Valid @ModelAttribute SellerDTO sellerDTO,
            @RequestParam("profileImage") MultipartFile profileImage) {

        try {
            // Validate required fields
            if (!profileImage.isEmpty()) {
                String filename = fileStorageService.storeFile(profileImage);
                sellerDTO.setProfileImage("/uploads/" + filename);
            }

            // Handle file upload
            if (profileImage != null && !profileImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(profileImage);
                sellerDTO.setProfileImage("/uploads/" + fileName);
            }

            // Convert to SellerDTO

            sellerDTO.setName(sellerDTO.getName());
            sellerDTO.setEmail(sellerDTO.getEmail());
            sellerDTO.setPassword(sellerDTO.getPassword());
            sellerDTO.setType(UserType.SELLER);
            sellerDTO.setPhone(sellerDTO.getPhone());
            sellerDTO.setAddress(sellerDTO.getAddress());
            sellerDTO.setProfileImage(sellerDTO.getProfileImage());
            sellerDTO.setNic(sellerDTO.getNic());
            sellerDTO.setBio(sellerDTO.getBio());

            // Set server-side values
            if (!profileImage.isEmpty()) {
                String filename = fileStorageService.storeFile(profileImage);
                sellerDTO.setProfileImage("/uploads/" + filename);

                // Save user and get result
                int result = userService.saveUser(sellerDTO);
                switch (result) {
                    case VarList.Created -> {
                        // Create UserDTO for token generation
                        UserDTO userDTO = new UserDTO();
                        userDTO.setEmail(sellerDTO.getEmail());
                        userDTO.setPassword(sellerDTO.getPassword());
                        userDTO.setType(sellerDTO.getType());

                        String token = jwtUtil.generateToken(userDTO);
                        AuthResponseDTO authDTO = new AuthResponseDTO();
                        authDTO.setEmail(sellerDTO.getEmail());
                        authDTO.setToken(token);
                        authDTO.setUserType(sellerDTO.getType());
                        authDTO.setExpiresAt(
                                jwtUtil.getExpirationDateFromToken(token)
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime()
                        );

                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(new ResponseDTO(VarList.Created, "Seller registration successful", authDTO));
                    }
                }


            catch(Exception e){
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ResponseDTO(VarList.Internal_Server_Error, "Seller registration failed: " + e.getMessage(), null));
                }
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
