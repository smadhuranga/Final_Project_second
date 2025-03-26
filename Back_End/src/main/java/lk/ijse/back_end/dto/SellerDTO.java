package lk.ijse.back_end.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lk.ijse.back_end.util.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SellerDTO extends UserDTO {
    private Long id;  // Add this field
    @NotBlank(message = "NIC is mandatory")
    private String nic;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;

    // Change to mutable collections
    private List<String> qualifications = new ArrayList<>();
    private List<Long> skillIds = new ArrayList<>();
    private List<Long> serviceIds = new ArrayList<>();
    private List<Long> ratingIds = new ArrayList<>();

    // Builder pattern setup (optional but recommended)
    public SellerDTO(String name, String email, String password, UserType type,
                     String phone, String address, String profileImage,
                     String nic, String bio) {
        super(name, email, password, type, phone, address, profileImage);
        this.nic = nic;
        this.bio = bio;
    }


}