package lk.ijse.back_end.service.impl;

import org.example.springwithjwt.dto.*;
import org.example.springwithjwt.entity.User;
import org.example.springwithjwt.repo.UserRepository;
import org.example.springwithjwt.service.UserService;
import org.example.springwithjwt.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException("User not found");
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getType().name()));
        return authorities;
    }

    @Override
    public UserDTO searchUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;
        return convertToDTO(user);
    }

    @Override
    public int saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        userRepository.save(modelMapper.map(userDTO, User.class));
        return VarList.Created;
    }

    private UserDTO convertToDTO(User user) {
        switch (user.getType()) {
            case CUSTOMER:
                CustomerDTO customerDTO = modelMapper.map(user, CustomerDTO.class);
                customerDTO.setOrderIds(user.getOrderIds());
                return customerDTO;
            case SELLER:
                SellerDTO sellerDTO = modelMapper.map(user, SellerDTO.class);
                sellerDTO.setNic(user.getNic());
                sellerDTO.setBio(user.getBio());
                sellerDTO.setQualifications(user.getQualifications());
                sellerDTO.setSkillIds(user.getSkillIds());
                return sellerDTO;
            case COORDINATOR:
                CoordinatorDTO coordinatorDTO = modelMapper.map(user, CoordinatorDTO.class);
                coordinatorDTO.setNic(user.getNic());
                coordinatorDTO.setQualifications(user.getQualifications());
                coordinatorDTO.setSkillIds(user.getSkillIds());
                return coordinatorDTO;
            case ADMIN:
                return modelMapper.map(user, AdminDTO.class);
            default:
                return modelMapper.map(user, UserDTO.class);
        }
    }
}