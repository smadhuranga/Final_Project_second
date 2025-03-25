//package lk.ijse.back_end.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//
//@Configuration
//public class JwtConfig {
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        byte[] secretKeyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
//        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
//        return NimbusJwtDecoder.withSecretKey(secretKey).build();
//    }
//}