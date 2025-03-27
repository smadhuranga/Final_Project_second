package lk.ijse.back_end.dto;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO extends UserDTO {
    @Null(message = "Order IDs must be null during registration")
    private List<Long> orderIds = null;



}
