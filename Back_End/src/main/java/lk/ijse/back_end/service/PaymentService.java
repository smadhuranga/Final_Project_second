package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.PaymentDTO;

public interface PaymentService {
    int savePayment(PaymentDTO paymentDTO);
    PaymentDTO getPaymentById(Long id);
}