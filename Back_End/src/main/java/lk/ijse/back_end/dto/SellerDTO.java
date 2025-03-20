package lk.ijse.back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lk.ijse.back_end.util.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SellerDTO extends UserDTO {

    @NotBlank(message = "NIC is mandatory")
    private String nic;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;

    // Initialize empty lists to avoid null pointers
    private List<String> qualifications = Collections.emptyList();
    private List<Long> skillIds = Collections.emptyList();
    private List<Long> serviceIds = Collections.emptyList();
    private List<Long> ratingIds = Collections.emptyList();

    // Builder pattern setup (optional but recommended)
    public SellerDTO(String name, String email, String password, UserType type,
                     String phone, String address, String profileImage,
                     String nic, String bio) {
        super(name, email, password, type, phone, address, profileImage);
        this.nic = nic;
        this.bio = bio;
    }
}