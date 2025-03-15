package lk.ijse.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerDTO extends UserDTO {
    private String nic;
    private String bio;
    private List<String> qualifications;
    private List<Long> skillIds;
    private List<Long> serviceIds;
    private List<Long> ratingIds;
}