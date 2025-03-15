package lk.ijse.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatorDTO extends UserDTO {
    private String nic;
    private List<String> qualifications;
    private List<Long> skillIds;
}