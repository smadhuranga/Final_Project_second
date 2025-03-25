package lk.ijse.back_end.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lk.ijse.back_end.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {
    private final Key secretKey;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        // Convert secret string to a secure Key
        byte[] keyBytes = Base64.getDecoder().decode(secret.trim());
        if (keyBytes.length < 64) {
            throw new IllegalArgumentException("Key must be 512 bits (64 bytes)");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

//    public String generateToken(UserDTO userDTO) {
//        return Jwts.builder()
//                .setHeaderParam("typ", "JWT")
//                .setSubject(userDTO.getEmail())  // Use unique identifier
//                .claim("type", userDTO.getType())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(secretKey, SignatureAlgorithm.HS512)
//                .compact();
//    }

    public Boolean canTokenBeRefreshed(String token) {
        try {
            final Date expiration = getAllClaimsFromToken(token).getExpiration();
            return expiration.after(new Date(System.currentTimeMillis() - 300000)); // 5 minute window
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)); // 10 hours

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


//    public String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//
//        // Extract roles without ROLE_ prefix
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .map(role -> role.replace("ROLE_", ""))
//                .collect(Collectors.toList());
//
//        claims.put("roles", roles);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userDetails.getUsername())
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }



    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", Collections.singletonList("ADMIN"));
        // Keep existing claims
        claims.put("type", "ADMIN");
        // Get roles without ROLE_ prefix
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        claims.put("roles", roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }



//    public Boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (MalformedJwtException ex) {
//            log.error("Invalid JWT structure: {}", ex.getMessage());
//            return false;
//        } catch (JwtException | IllegalArgumentException ex) {
//            log.error("JWT validation failed: {}", ex.getMessage());
//            return false;
//        }
//    }



    public Boolean validateToken(String token) {
        try {
            // Add validation logging
            System.out.println("Validating token: " + token.substring(0, 20) + "...");

            Claims claims = getAllClaimsFromToken(token);
            System.out.println("Token claims: " + claims);

            return true;
        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT format: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Expired JWT: " + e.getMessage());
        }
        return false;
    }


    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256) // Use Key object
                .compact();
    }



    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // Match signing key
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public String getUserTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }
}