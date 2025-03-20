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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

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

}