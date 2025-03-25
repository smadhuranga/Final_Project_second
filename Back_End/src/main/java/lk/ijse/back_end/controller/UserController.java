package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.UserType;
import lk.ijse.back_end.util.VarList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@CrossOrigin
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserByEmail(@PathVariable String email) {
        try {
            UserDTO userDTO = userService.searchUser(email);
            if(userDTO != null) {
                return ResponseEntity.ok(
                        new ResponseDTO<>(VarList.OK, "Success", userDTO)
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(VarList.Not_Found, "User Not Found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Changed to hasAuthority with full role name
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserType type) {

        try {
            List<UserDTO> users = userService.findAllUsers()
                    .stream()
                    .filter(u -> type == null || u.getType() == type)
                    .filter(u -> search == null ||
                            u.getName().contains(search) ||
                            u.getEmail().contains(search))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Success", users)
            );
        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error,
                            "Error: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO<?> updateUserStatus(
            @PathVariable String id,
            @RequestParam boolean active) {

        userService.updateUserStatus(id, active);
        return new ResponseDTO<>(200, "Status updated", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseDTO<>(204, "User deleted", null);
    }





    @PutMapping("/{userType}s/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO<?> updateUserStatus(
            @PathVariable String userType,
            @PathVariable String userId,
            @RequestBody Map<String, String> statusRequest
    ) {
        userService.updateUserStatus(userType, userId, statusRequest.get("status"));
        return new ResponseDTO<>(200, "Status updated successfully", null);
    }
}


















//package lk.ijse.back_end.controller;
//
//import lk.ijse.back_end.dto.ResponseDTO;
//import lk.ijse.back_end.dto.UserDTO;
//import lk.ijse.back_end.service.UserService;
//import lk.ijse.back_end.service.impl.UserServiceImpl;
//import lk.ijse.back_end.util.UserType;
//import lk.ijse.back_end.util.VarList;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/admin/users")
//@CrossOrigin
//@PreAuthorize("hasRole('ADMIN')")
//
//public class UserController {
//
//    private final UserServiceImpl userService;
//
//    @Autowired
//    public UserController(UserServiceImpl userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping("/{email}")
//    public ResponseEntity<ResponseDTO<UserDTO>> getUserByEmail(@PathVariable String email) {
//        try {
//            UserDTO userDTO = userService.searchUser(email);
//            if(userDTO != null) {
//                return ResponseEntity.ok(
//                        new ResponseDTO<>(VarList.OK, "Success", userDTO)
//                );
//            }
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ResponseDTO<>(VarList.Not_Found, "User Not Found", null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseDTO<>(VarList.Internal_Server_Error,
//                            "Error: " + e.getMessage(), null));
//        }
//    }
//
//
//
//    @GetMapping
//    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers(
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) UserType type) {
//
//        try {
//            List<UserDTO> users = userService.findAllUsers()
//                    .stream()
//                    .filter(u -> type == null || u.getType() == type)
//                    .filter(u -> search == null ||
//                            u.getName().contains(search) ||
//                            u.getEmail().contains(search))
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(
//                    new ResponseDTO<>(VarList.OK, "Success", users)
//            );
//        } catch (Exception e) {
//            log.error("Error retrieving users: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseDTO<>(VarList.Internal_Server_Error,
//                            "Error: " + e.getMessage(), null));
//        }
//    }
//
//    @PutMapping("/{id}/status")
//    public ResponseDTO<?> updateUserStatus(
//            @PathVariable String id,
//            @RequestParam boolean active) {
//
//        userService.updateUserStatus(id, active);
//        return new ResponseDTO<>(200, "Status updated", null);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseDTO<?> deleteUser(@PathVariable String id) {
//        userService.deleteUser(id);
//        return new ResponseDTO<>(204, "User deleted", null);
//    }
//
//}