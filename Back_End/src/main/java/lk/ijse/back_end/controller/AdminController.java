package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.repository.OrdersRepo;
import lk.ijse.back_end.repository.PaymentRepo;
import lk.ijse.back_end.service.CoordinatorService;
import lk.ijse.back_end.service.CustomerService;
import lk.ijse.back_end.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private OrdersRepo ordersRepo;

    @GetMapping("/analytics/earnings")
    public ResponseDTO<Map<String, Object>> getEarningsAnalytics() {
        Map<String, Object> data = new HashMap<>();

        // Get monthly earnings data
        List<Object[]> monthlyData = paymentRepo.getMonthlyEarnings();
        List<Double> monthlyEarnings = new ArrayList<>(Collections.nCopies(12, 0.0));

        for (Object[] item : monthlyData) {
            int month = (int) item[0];
            BigDecimal amount = (BigDecimal) item[1];
            monthlyEarnings.set(month - 1, amount.doubleValue());
        }

        // Get total earnings
        BigDecimal total = paymentRepo.getTotalCompletedPayments();

        data.put("monthly", monthlyEarnings);
        data.put("total", total != null ? total.doubleValue() : 0.0);

        return new ResponseDTO<>(200, "Success", data);
    }

    @GetMapping("/analytics/projects")
    public ResponseDTO<Map<String, Long>> getProjectStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("ongoing", ordersRepo.countByStatus("ongoing"));
        stats.put("completed", ordersRepo.countByStatus("completed"));
        stats.put("pending", ordersRepo.countByStatus("pending"));
        return new ResponseDTO<>(200, "Success", stats);
    }

    @DeleteMapping("/{userType}s/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDTO<?> deleteUser(
            @PathVariable String userType,
            @PathVariable String id
    ) {
        switch(userType.toLowerCase()) {
            case "customer":
                CustomerService.deleteCustomer(id);
                break;
            case "seller":
                SellerService.deleteSeller(id);
                break;
            case "coordinator":
                CoordinatorService.deleteCoordinator(id);
                break;
            default:
                throw new IllegalArgumentException("Invalid user type");
        }
        return new ResponseDTO<>(200, "User deleted successfully", null);
    }
}