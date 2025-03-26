package lk.ijse.back_end.controller;

import jakarta.validation.Valid;
import lk.ijse.back_end.dto.*;

import lk.ijse.back_end.repository.RatingRepo;
import lk.ijse.back_end.service.OrderService;
import lk.ijse.back_end.service.RatingService;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.JwtUtil;
import lk.ijse.back_end.util.OrderStatus;
import lk.ijse.back_end.util.UserType;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RatingService ratingService;
    private final OrderService orderService;  // Add this

    @Autowired
    public SellerController(UserService userService,
                            JwtUtil jwtUtil,
                            RatingService ratingService,
                            OrderService orderService) {  // Add this parameter
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.ratingService = ratingService;
        this.orderService = orderService;  // Initialize here
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

                    String token = jwtUtil.generateToken(authUserDTO);
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

    // SellerController.java
    // SellerController.java
    @GetMapping("/dashboard")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getSellerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String email = userDetails.getUsername();
            SellerDTO seller = (SellerDTO) userService.findUserByEmail(email);

            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("totalEarnings", calculateTotalEarnings(seller));
            dashboardData.put("activeOrders", countActiveOrders(seller));
            dashboardData.put("avgRating", calculateAverageRating(seller));

            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Dashboard data", dashboardData)
            );
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(VarList.Not_Found, "Seller not found", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Error fetching dashboard data", null));
        }
    }

    private double calculateTotalEarnings(SellerDTO seller) {
        List<OrderDTO> completedOrders = orderService.getOrdersBySellerAndStatus(
                seller.getId(), // Use Long ID instead of email
                OrderStatus.COMPLETED
        );
        return completedOrders.stream()
                .mapToDouble(OrderDTO::getAmount)
                .sum();
    }

    private int countActiveOrders(SellerDTO seller) {
        return orderService.countOrdersBySellerAndStatus(
                seller.getId(), // Use Long ID instead of email
                OrderStatus.ACTIVE
        );
    }

    private double calculateAverageRating(SellerDTO seller) {
        if (seller.getRatingIds() == null || seller.getRatingIds().isEmpty()) {
            return 0.0;
        }

        double total = seller.getRatingIds().stream()
                .mapToDouble(ratingId -> ratingService.getRatingValue(ratingId))
                .filter(value -> value > 0)
                .sum();

        return total / seller.getRatingIds().size();
    }



    @GetMapping("/me")
    public ResponseEntity<ResponseDTO<SellerDTO>> getCurrentSeller(
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String email = userDetails.getUsername();
            SellerDTO seller = (SellerDTO) userService.findUserByEmail(email);
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Seller profile", seller)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // ServiceController.java
    @GetMapping
    public ResponseEntity<ResponseDTO<List<ServiceDTO>>> getAllServices() {
        // Return all services
        return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Services fetched successfully", null));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createService(@RequestBody ServiceDTO serviceDTO) {
        // Create service logic
        return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Service created successfully", null));
    }
}