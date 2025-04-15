package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.CustomerDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.SellerDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
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
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable String id) {
        try {

            if (!id.matches("\\d+")) {
                return ResponseEntity.badRequest()
                        .body(new ResponseDTO(VarList.Bad_Request, "Invalid ID format", null));
            }

            Long userId = Long.parseLong(id);
            int result = userService.deleteUser(userId);

            if (result == VarList.OK) {
                return ResponseEntity.ok(
                        new ResponseDTO(VarList.OK, "User deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(VarList.Bad_Request, "Invalid ID format", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error,
                            "Error deleting user: " + e.getMessage(), null));
        }
    }
    @GetMapping("/admin/customers")
    public ResponseEntity<ResponseDTO<List<CustomerDTO>>> getAllCustomers() {
        try {
            List<CustomerDTO> customers = userService.getAllCustomers();
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Customers retrieved", customers)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @GetMapping("/admin/sellers")
    public ResponseEntity<ResponseDTO<List<SellerDTO>>> getAllSellers() {
        try {
            List<SellerDTO> sellers = userService.getAllSellers();
            return ResponseEntity.ok(
                    new ResponseDTO<>(200, "Sellers retrieved", sellers)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ResponseDTO<>(500, "Error: " + e.getMessage(), null));
        }
    }

    @PatchMapping("/admin/users/{id}/status")
    public ResponseEntity<ResponseDTO> toggleUserStatus(@PathVariable Long id) {
        try {
            int result = userService.toggleUserStatus(id);
            if(result == VarList.OK) {
                return ResponseEntity.ok(
                        new ResponseDTO(VarList.OK, "Status updated", null)
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }



}