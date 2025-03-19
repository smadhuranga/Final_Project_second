package lk.ijse.back_end.config;


import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.entity.Customer;
import lk.ijse.back_end.entity.Seller;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setAmbiguityIgnored(true);

        // Explicit mapping for UserDTO to User subclasses
        mapper.createTypeMap(UserDTO.class, Customer.class)
                .addMapping(UserDTO::getPassword, Customer::setPassword);
        mapper.createTypeMap(UserDTO.class, Seller.class)
                .addMapping(UserDTO::getPassword, Seller::setPassword);
        // Repeat for other subclasses

        return mapper;
    }
}