package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {
    int savePayment(PaymentDTO paymentDTO);
    PaymentDTO getPaymentById(Long id);
    List<PaymentDTO> getAllPayments();
    PaymentDTO updatePaymentStatus(Long id, String status);

}