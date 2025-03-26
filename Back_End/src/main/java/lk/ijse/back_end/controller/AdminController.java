package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/users")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Success", users)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Error: " + e.getMessage(), null));
        }
    }
}