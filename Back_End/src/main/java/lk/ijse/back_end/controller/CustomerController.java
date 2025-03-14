package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.CustomerDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin
public class CustomerController {

    private final UserServiceImpl userService;

    @Autowired
    public CustomerController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            int result = userService.saveUser(customerDTO);
            if(result == VarList.Created) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.Created, "Success", null), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Conflict, "Email Already Exists", null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}