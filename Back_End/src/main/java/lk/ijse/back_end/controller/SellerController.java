package lk.ijse.back_end.controller;

import jakarta.validation.Valid;
import lk.ijse.back_end.dto.*;

import lk.ijse.back_end.repository.RatingRepo;
import lk.ijse.back_end.service.OrderService;
import lk.ijse.back_end.service.RatingService;
import lk.ijse.back_end.service.ServiceService;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final OrderService orderService;
    private final ServiceService serviceService;
    private final EmailUtil emailUtil;

    @Autowired
    public SellerController(UserService userService,
                            JwtUtil jwtUtil,
                            RatingService ratingService,
                            OrderService orderService,
                            ServiceService serviceService,
                            EmailUtil emailUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.ratingService = ratingService;
        this.orderService = orderService;
        this.serviceService = serviceService;
        this.emailUtil = emailUtil;
    }
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerSeller(
            @Valid @RequestPart("data") SellerDTO sellerDTO,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                String imageUrl = userService.storeProfileImage(sellerDTO.getEmail(), profileImage);
                sellerDTO.setProfileImage(imageUrl);
            }


            sellerDTO.setCreatedAt(LocalDateTime.now());
            sellerDTO.setType(UserType.SELLER);
            sellerDTO.setSkillIds(Collections.emptyList());
            sellerDTO.setServiceIds(Collections.emptyList());
            sellerDTO.setRatingIds(Collections.emptyList());
            sellerDTO.setQualifications(Collections.emptyList());

            int result = userService.saveUser(sellerDTO);

            switch (result) {
                case VarList.Created -> {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Registration failed: " + e.getMessage(), null));
        }
    }

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

    @PutMapping("/me")
    public ResponseEntity<ResponseDTO<SellerDTO>> updateSellerProfile(
            @RequestBody SellerDTO sellerDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            SellerDTO updatedSeller = userService.updateSellerProfile(email, sellerDTO);
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Profile updated successfully", updatedSeller)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @GetMapping("/services")
    public ResponseEntity<ResponseDTO<List<ServiceDTO>>> getServicesByCategory(
            @RequestParam Long categoryId) {

        try {

            List<ServiceDTO> services = serviceService.getServicesByCategoryId(categoryId);
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Services fetched successfully", services)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createService(@RequestBody ServiceDTO serviceDTO) {
        return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Service created successfully", null));
    }
    @PostMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO(VarList.Bad_Request, "File cannot be empty", null));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO(VarList.Bad_Request, "Only image files are allowed", null));
            }

            String email = userDetails.getUsername();
            String imageUrl = userService.storeProfileImage(email, file);
            SellerDTO seller = (SellerDTO) userService.findUserByEmail(email);
            seller.setProfileImage(imageUrl);
            userService.updateUser(seller);

            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Image uploaded successfully", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error processing image: " + e.getMessage(), null));
        }
    }

    private double calculateTotalEarnings(SellerDTO seller) {
        List<OrderDTO> completedOrders = orderService.getOrdersBySellerAndStatus(
                seller.getId(),
                OrderStatus.COMPLETED
        );
        return completedOrders.stream()
                .mapToDouble(OrderDTO::getAmount)
                .sum();
    }

    private int countActiveOrders(SellerDTO seller) {
        return orderService.countOrdersBySellerAndStatus(
                seller.getId(),
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

    @PostMapping("/notify-chat")
    public ResponseEntity<ResponseDTO> notifyChatInitiation(
            @RequestParam String sellerEmail,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {

            String customerEmail = userDetails.getUsername();
            UserDTO customer = userService.searchUser(customerEmail);


            SellerDTO seller = (SellerDTO) userService.findUserByEmail(sellerEmail);


            emailUtil.sendChatNotification(
                    seller.getEmail(),
                    customer.getName(),
                    customer.getEmail()
            );

            return ResponseEntity.ok(
                    new ResponseDTO(VarList.OK, "Seller notified successfully", null)
            );
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        } catch (EmailException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ResponseDTO(
                            VarList.Service_Unavailable,
                            "Email service temporarily unavailable",
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            VarList.Internal_Server_Error,
                            "Error sending notification: " + e.getMessage(),
                            null
                    ));
        }
    }
}