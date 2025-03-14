package lk.ijse.back_end.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoordinatorDTO extends UserDTO {
    private String nic;
    private List<String> qualifications;
    private List<Long> skillIds;
}