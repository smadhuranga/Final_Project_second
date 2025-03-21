//package lk.ijse.back_end.util;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.SignatureAlgorithm;
//import java.util.Base64;
//
//public class KeyGenerator {
//    public static void main(String[] args) {
//        // Generate a secure key for HS512
//        var key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//        // Encode the key in Base64
//        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
//        System.out.println("Generated Secret Key: " + base64Key);
//    }
//}