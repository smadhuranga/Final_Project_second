package lk.ijse.back_end.controller;


import jakarta.validation.Valid;
import jdk.jfr.ContentType;
import lk.ijse.back_end.dto.AuthResponseDTO;
import lk.ijse.back_end.dto.CustomerDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.JwtUtil;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        try {
            // Log incoming request for debugging
            System.out.println("Received customerDTO: " + customerDTO);

            // Set server-side values
          customerDTO.setCreatedAt(LocalDateTime.now());
            customerDTO.setOrderIds(Collections.emptyList());

            int result = userService.saveUser(customerDTO);
            switch (result) {
                case VarList.Created -> {
                    String token = jwtUtil.generateToken(customerDTO);
                    AuthResponseDTO authDTO = new AuthResponseDTO(customerDTO.getEmail(), token);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Success", authDTO));
                }
                case VarList.Conflict -> {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ResponseDTO(VarList.Conflict, "Email Already Exists", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseDTO(VarList.Bad_Request, "Invalid Request", null));
                }
            }
        } catch (Exception e) {
            // Log exception to help with debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

}