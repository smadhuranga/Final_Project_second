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
}