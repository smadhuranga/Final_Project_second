package lk.ijse.back_end.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.back_end.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final String secretKey;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil,
                     @Lazy UserDetailsService userDetailsService,
                     @Value("${jwt.secret}") String secretKey) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.secretKey = secretKey;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        try {
            String header = request.getHeader("Authorization");

            // Skip filtering for permitted endpoints
            if (shouldNotFilter(request)) {
                chain.doFilter(request, response);
                return;
            }

            if (header == null || !header.startsWith("Bearer ")) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
                return;
            }

            String token = header.substring(7).trim();

            if (token.isEmpty() || "undefined".equalsIgnoreCase(token)) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token format");
                return;
            }

            if (jwtUtil.validateToken(token)) {
                setSecurityContext(request, token);
                chain.doFilter(request, response);
            } else {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token");
            }

        } catch (Exception e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication failed: " + e.getMessage());
        }
    }
    private boolean isProtectedEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/auth/login")
                && !path.startsWith("/api/v1/customers/register");
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{ \"error\": \"" + message + "\" }");
    }
    private String extractToken(String header) {
        if (header == null) return null;
        String[] parts = header.split("\\s+");
        return parts.length == 2 ? parts[1].trim() : null;
    }

    private void validateTokenStructure(String token) {
        if (!token.matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")) {
            throw new JwtException("Invalid JWT structure");
        }
    }

    private int countDots(String token) {
        return token.length() - token.replace(".", "").length();
    }
    private boolean isValidAuthorizationHeader(String header) {
        return header != null && header.startsWith("Bearer ");
    }

    public Boolean validateToken(String token) {
        try {
            // Validate token format first
            if (token.split("\\.").length != 3) {
                throw new JwtException("Invalid token structure");
            }

            // Then validate cryptographic signature
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
           logger.error("Invalid token: " + e.getMessage());
            return false;
        }
    }
//    private void setSecurityContext(HttpServletRequest request, String token) {
//        Claims claims = jwtUtil.getAllClaimsFromToken(token);
//        String username = claims.getSubject();
//        String userType = claims.get("roles", String.class);
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//        if (userDetails != null) {
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            Collections.singletonList(() -> "ROLE_" + userType)
//                    );
//
//            authentication.setDetails(
//                    new WebAuthenticationDetailsSource().buildDetails(request)
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//    }
private void setSecurityContext(HttpServletRequest request, String token) {
    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String username = claims.getSubject();
    String userType = claims.get("roles", String.class);

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (userDetails != null && userDetails.isEnabled()) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // Use actual authorities
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

    private void handleAuthenticationError(HttpServletResponse response, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(
                String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message)
        );
        logger.warn( message);
    }

    private void handleUnexpectedError(HttpServletResponse response, Exception ex)
            throws IOException {
        logger.error("System Error: ", ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.getWriter().write("Internal server error");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return new AntPathRequestMatcher("/api/v1/auth/login").matches(request) ||
                new AntPathRequestMatcher("/api/v1/customers/register").matches(request) ||
                new AntPathRequestMatcher("/api/v1/sellers/register").matches(request) ||
        new AntPathRequestMatcher("/login.html").matches(request)||
                new AntPathRequestMatcher( "/pages/").matches(request) ||
                new AntPathRequestMatcher( "/css/").matches(request) ||
                new AntPathRequestMatcher( "/js/").matches(request) ||
                new AntPathRequestMatcher( "/images/").matches(request);
    }

    private String extractAndCleanToken(String header) {
        return header.substring(7).trim(); // Remove "Bearer" prefix and whitespace
    }



    private boolean isValidTokenStructure(String token) {
        if (token == null || token.isEmpty()) return false;
        return token.chars().filter(c -> c == '.').count() == 2;
    }
}