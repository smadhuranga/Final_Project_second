package lk.ijse.back_end.service.impl;



import lk.ijse.back_end.dto.*;
import lk.ijse.back_end.entity.*;
import lk.ijse.back_end.repository.UserRepo;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.util.CustomUserDetails;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;

import java.util.Set;



@Service
@Primary
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // Remove any WebSecurityConfig dependencies
    @Autowired
    public UserServiceImpl(UserRepo userRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        System.out.println("Received customerDTO: " + userDTO);

        User userEntity = createUserEntity(userDTO);
        userRepository.save(userEntity);
        return VarList.Created;
    }

    private User createUserEntity(UserDTO userDTO) {
        return switch (userDTO.getType()) {
            case CUSTOMER -> modelMapper.map(userDTO, Customer.class);
            case SELLER -> modelMapper.map(userDTO, Seller.class);
            case COORDINATOR -> modelMapper.map(userDTO, Coordinator.class);
            case ADMIN -> modelMapper.map(userDTO, Admin.class);
            default -> throw new IllegalArgumentException("Invalid user type");
        };
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
//        dto.setOrderIds(customer.getOrderIds());
        return dto;
    }

    private SellerDTO convertSeller(Seller seller) {
        SellerDTO dto = modelMapper.map(seller, SellerDTO.class);
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
}