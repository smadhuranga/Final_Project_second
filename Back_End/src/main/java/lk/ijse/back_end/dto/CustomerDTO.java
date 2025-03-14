package lk.ijse.back_end.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDTO extends UserDTO {
    private List<Long> orderIds;
}
