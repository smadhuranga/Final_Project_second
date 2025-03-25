//package lk.ijse.back_end.controller;
//
//import lk.ijse.back_end.dto.ResponseDTO;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@DeleteMapping("/{userType}s/{id}")
//@PreAuthorize("hasRole('ADMIN')")
//public ResponseDTO<?> deleteUser(
//        @PathVariable String userType,
//        @PathVariable String id
//) {
//    switch(userType.toLowerCase()) {
//        case "customer":
//            customerService.deleteCustomer(id);
//            break;
//        case "seller":
//            sellerService.deleteSeller(id);
//            break;
//        case "coordinator":
//            coordinatorService.deleteCoordinator(id);
//            break;
//        default:
//            throw new IllegalArgumentException("Invalid user type");
//    }
//    return new ResponseDTO<>(200, "User deleted successfully", null);
//}