package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.ServiceCategoryDTO;
import lk.ijse.back_end.dto.ServiceDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.service.ServiceCategoryService;
import lk.ijse.back_end.service.ServiceService;
import lk.ijse.back_end.service.impl.UserServiceImpl;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ServiceService serviceService;

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

    // In AdminController.java

    @GetMapping("/services")
    public ResponseEntity<ResponseDTO<List<ServiceDTO>>> getAllServices() {
        try {
            List<ServiceDTO> services = serviceService.getAllServices();
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Success", services)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/services")
    public ResponseEntity<ResponseDTO> createService(@RequestBody ServiceDTO serviceDTO) {
        try {
            int result = serviceService.saveService(serviceDTO);
            if(result == VarList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Service created", null));
            }
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(VarList.Bad_Request, "Error creating service", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ResponseDTO> updateService(@PathVariable Long id, @RequestBody ServiceDTO serviceDTO) {
        try {
            serviceDTO.setId(id);
            int result = serviceService.updateService(serviceDTO);
            if(result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Service updated", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<ResponseDTO> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Service deleted", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }








    @Autowired
    private ServiceCategoryService categoryService;

    // Category endpoints
    @GetMapping("/categories")
    public ResponseEntity<ResponseDTO<List<ServiceCategoryDTO>>> getAllCategories() {
        try {
            List<ServiceCategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Success", categories)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<ResponseDTO> createCategory(@RequestBody ServiceCategoryDTO categoryDTO) {
        try {
            int result = categoryService.saveServiceCategory(categoryDTO);
            if(result == VarList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Category created", null));
            }
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO(VarList.Bad_Request, "Error creating category", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ResponseDTO> updateCategory(@PathVariable Long id, @RequestBody ServiceCategoryDTO categoryDTO) {
        try {
            categoryDTO.setId(id);
            int result = categoryService.updateCategory(categoryDTO);
            if(result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Category updated", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable Long id) {
        try {
            int result = categoryService.deleteCategory(id);
            if(result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Category deleted", null));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // Add to AdminController.java
    @GetMapping("/categories/{id}")
    public ResponseEntity<ResponseDTO<ServiceCategoryDTO>> getCategoryById(@PathVariable Long id) {
        try {
            ServiceCategoryDTO category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Success", category)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseDTO<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Success", user)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, "Error: " + e.getMessage(), null));
        }
    }
}