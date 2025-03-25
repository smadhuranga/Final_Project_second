package lk.ijse.back_end.service.impl;



import lk.ijse.back_end.dto.PaymentDTO;
import lk.ijse.back_end.entity.Payment;
import lk.ijse.back_end.repository.PaymentRepo;
import lk.ijse.back_end.service.PaymentService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepo paymentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int savePayment(PaymentDTO paymentDTO) {
        paymentRepository.save(modelMapper.map(paymentDTO, Payment.class));
        return VarList.Created;
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .orElse(null);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO updatePaymentStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        return modelMapper.map(updatedPayment, PaymentDTO.class);

    }
}