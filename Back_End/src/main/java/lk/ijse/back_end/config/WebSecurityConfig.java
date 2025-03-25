//package lk.ijse.back_end.config;
//
//import lk.ijse.back_end.util.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class WebSecurityConfig {
//
//    private final JwtFilter jwtFilter;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//    @Autowired
//    public WebSecurityConfig(JwtFilter jwtFilter) {
//        this.jwtFilter = jwtFilter;
//    }
//
//    // Add this method
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1/customers/register",
//                                "/api/v1/auth/login"
//                                ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        return http
////                .csrf(AbstractHttpConfigurer::disable)
////                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add CORS configuration
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers(HttpMethod.OPTIONS, "/api/v1/**").permitAll() // Permit OPTIONS for all endpoints
////                        .requestMatchers(HttpMethod.POST, "/api/v1/customers/register").permitAll()
////                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
////                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/authenticate").permitAll()
////                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-password").permitAll()
////                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-pw-otp").permitAll()
////                        .requestMatchers(HttpMethod.GET, "/api/v1/user/profile").authenticated()
////                        .requestMatchers(HttpMethod.PUT, "/api/v1/user/profile").authenticated()
////                        .requestMatchers(HttpMethod.POST, "/api/v1/user/profile/image").authenticated()
////                        .requestMatchers(HttpMethod.DELETE, "/api/v1/user/profile/image").authenticated()
////                        .anyRequest().authenticated()
////                )
////                .sessionManagement(session -> session
////                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                )
////                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
////                .build();
////    }
////
////    @Bean
////    public CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        configuration.setAllowedOrigins(Arrays.asList("*")); // Or specify your frontend origin
////        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
////        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "*"));
////        configuration.setAllowCredentials(false);
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/api/v1/**", configuration);
////        return source;
////    }
////public  CorsConfigurationSource corsConfigurationSource() {
////    CorsConfiguration configuration = new CorsConfiguration();
////    configuration.setAllowedOrigins(List.of("http://localhost:63342")); // Exact origin
////    configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
////    configuration.setAllowedHeaders(List.of("*"));
////    configuration.setAllowCredentials(true);
////    configuration.setExposedHeaders(List.of("Authorization")); // If using JWT
////
////    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////    source.registerCorsConfiguration("/**", configuration);
////    return source;
////}
//
//}


package lk.ijse.back_end.config;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtFilter jwtFilter;

    @Autowired
    public WebSecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/customers/register",
                                "/api/v1/sellers/register",
                                "/uploads/**",
                                "/login.html",
                                "/pages/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/api/v1/auth/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers(
//                                "/api/v1/customers/register",
//                                "/api/v1/sellers/register",
//                                "/uploads/**",
//                                "/login.html",
//                                "/pages/**",
//                                "/css/**",
//                                "/js/**",
//                                "/images/**",
//                                "/api/v1/auth/**",
//                                "/api/v1/admin/**"
//                        ).permitAll()
//
//                        // Admin-specific endpoints
//                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//
//                        // Authenticated endpoints
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:63342",
                "http://localhost:8080",
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }






}