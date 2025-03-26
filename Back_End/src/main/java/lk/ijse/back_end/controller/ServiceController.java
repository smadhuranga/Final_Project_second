package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.ServiceDTO;
import lk.ijse.back_end.service.impl.ServiceServiceImpl;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@CrossOrigin
public class ServiceController {

    private final ServiceServiceImpl serviceService;

    @Autowired
    public ServiceController(ServiceServiceImpl serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createService(@RequestBody ServiceDTO serviceDTO) {
        try {
            int result = serviceService.saveService(serviceDTO);
            if(result == VarList.Created) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.Created, "Service Created", serviceDTO), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Conflict, "Service Exists", null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getServiceById(@PathVariable Long id) {
        try {
            ServiceDTO serviceDTO = serviceService.getServiceById(id);
            if(serviceDTO != null) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.OK, "Success", serviceDTO), HttpStatus.OK);
            }
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Not_Found, "Service Not Found", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping
    public ResponseEntity<ResponseDTO<List<ServiceDTO>>> getAllServices() {
        try {
            // Implement service retrieval logic based on your implementation
            List<ServiceDTO> services = Collections.emptyList(); // Replace with actual implementation
            return ResponseEntity.ok(
                    new ResponseDTO<>(VarList.OK, "Services retrieved", services)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }
}