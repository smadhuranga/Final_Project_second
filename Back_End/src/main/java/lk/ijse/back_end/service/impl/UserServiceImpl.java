package lk.ijse.back_end.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lk.ijse.back_end.dto.*;
import lk.ijse.back_end.entity.*;
import lk.ijse.back_end.repository.UserRepo;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.CustomUserDetails;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Primary
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final Cloudinary cloudinary;



    // Remove any WebSecurityConfig dependencies
    @Autowired
    public UserServiceImpl(UserRepo userRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           Cloudinary cloudinary) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.cloudinary = cloudinary;
    }

    @Override

    public UserDetails loadUserByUsername(String email) {

        User user = userRepository.findByEmail(email)

                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getPassword() == null || user.getPassword().isEmpty()) {

            throw new AuthenticationCredentialsNotFoundException("No password set");

        }

        return new CustomUserDetails(user);

    }

    private Set<SimpleGrantedAuthority> getAuthorities(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getType().name()));
        return authorities;
    }

    @Override
    public UserDTO searchUser(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public int saveUser(UserDTO userDTO) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                return VarList.Conflict; // Changed from Not_Acceptable to match controller
            }

            // Validate required fields
            if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                return VarList.Bad_Request;
            }

            // Encode password
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(encodedPassword);

            // Ensure all required fields are set
            if (userDTO.getCreatedAt() == null) {
                userDTO.setCreatedAt(LocalDateTime.now());
            }


            User userEntity = createUserEntity(userDTO);
            userRepository.save(userEntity);
            return VarList.Created;

        } catch (Exception e) {
            e.printStackTrace();
            return VarList.Internal_Server_Error;
        }
    }

    private User createUserEntity(UserDTO userDTO) {
        User userEntity = switch (userDTO.getType()) {
            case CUSTOMER -> {
                Customer customer = modelMapper.map(userDTO, Customer.class);
                customer.setPassword(userDTO.getPassword()); // Explicit set
                yield customer;
            }
            case SELLER -> {
                Seller seller = modelMapper.map(userDTO, Seller.class);
                seller.setPassword(userDTO.getPassword());
                yield seller;
            }
            case COORDINATOR -> {
                Coordinator coordinator = modelMapper.map(userDTO, Coordinator.class);
                coordinator.setPassword(userDTO.getPassword());
                yield coordinator;
            }
            case ADMIN -> {
                Admin admin = modelMapper.map(userDTO, Admin.class);
                admin.setPassword(userDTO.getPassword());
                yield admin;
            }
            default -> throw new IllegalArgumentException("Invalid user type");
        };
        return userEntity;
    }

    private UserDTO convertToDTO(User user) {
        return switch (user.getType()) {
            case CUSTOMER -> convertCustomer((Customer) user);
            case SELLER -> convertSeller((Seller) user);
            case COORDINATOR -> convertCoordinator((Coordinator) user);
            case ADMIN -> modelMapper.map(user, AdminDTO.class);
            default -> modelMapper.map(user, UserDTO.class);
        };
    }

    private CustomerDTO convertCustomer(Customer customer) {
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);
        dto.setOrderIds(customer.getOrderIds());
        return dto;
    }

    private SellerDTO convertSeller(Seller seller) {
        SellerDTO dto = modelMapper.map(seller, SellerDTO.class);
        dto.setId(seller.getId()); // Add this line to set the ID
        dto.setNic(seller.getNic());
        dto.setBio(seller.getBio());
        dto.setQualifications(seller.getQualifications());
        dto.setSkillIds(seller.getSkillIds());
        return dto;
    }

    private CoordinatorDTO convertCoordinator(Coordinator coordinator) {
        CoordinatorDTO dto = modelMapper.map(coordinator, CoordinatorDTO.class);
        dto.setNic(coordinator.getNic());
        dto.setQualifications(coordinator.getQualifications());
        dto.setSkillIds(coordinator.getSkillIds());
        return dto;
    }

    @Override
    public UserDTO findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(user instanceof Seller) {
            return convertSeller((Seller) user);
        }

        return convertToDTO(user);
    }

    @Override
    public int updateUser(UserDTO userDTO) {
        try {
            User user = userRepository.findByEmail(userDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setPhone(userDTO.getPhone());
            user.setAddress(userDTO.getAddress());
            user.setProfileImage(userDTO.getProfileImage());
            userRepository.save(user);
            return VarList.OK;
        } catch (Exception e) {
            return VarList.Bad_Request;
        }
    }



    @Override
    public String storeProfileImage(String email, MultipartFile file) {
        try {
            // Upload parameters
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", "profile_images/" + email.replace("@", "_"), // Safe public ID
                    "overwrite", true,
                    "resource_type", "image",
                    "folder", "profile_images",
                    "allowed_formats", new String[]{"jpg", "png", "jpeg", "gif"},
                    "transformation", new Transformation()
                            .width(500)
                            .height(500)
                            .crop("fill")
                            .gravity("face")
            );

            // Upload the image
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            System.out.println("Upload result: " + uploadResult.get("secure_url"));
            // Return the secure URL of the uploaded image
            return String.valueOf(uploadResult.get("secure_url"));
        } catch (IOException e) {
            try {
                throw new IOException("Failed to upload image to Cloudinary", e);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    @Override
    public int changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return VarList.Unauthorized;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return VarList.OK;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}