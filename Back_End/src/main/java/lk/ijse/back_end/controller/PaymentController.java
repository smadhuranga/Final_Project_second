package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.PaymentDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.service.impl.PaymentServiceImpl;
import lk.ijse.back_end.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@PreAuthorize("hasRole('ADMIN')")

@CrossOrigin
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @Autowired
    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> processPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            int result = paymentService.savePayment(paymentDTO);
            if(result == VarList.Created) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.Created, "Payment Processed", paymentDTO), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Bad_Request, "Invalid Payment", null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseDTO<List<PaymentDTO>> getAllPayments() {
        return new ResponseDTO<>(200, "Success", paymentService.getAllPayments());
    }

    @PutMapping("/{id}/status")
    public ResponseDTO<PaymentDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return new ResponseDTO<>(200, "Updated",
                paymentService.updatePaymentStatus(id, status));
    }

}