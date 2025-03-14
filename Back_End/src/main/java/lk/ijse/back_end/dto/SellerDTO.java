package lk.ijse.back_end.dto;

import lombok.Data;

import java.util.List;

@Data
public class SellerDTO extends UserDTO {
    private String nic;
    private String bio;
    private List<String> qualifications;
    private List<Long> skillIds;
    private List<Long> serviceIds;
    private List<Long> ratingIds;
}