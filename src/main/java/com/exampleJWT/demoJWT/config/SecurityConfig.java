package com.exampleJWT.demoJWT.config;

import com.exampleJWT.demoJWT.security.JWTAthenticationEntryPoint;
import com.exampleJWT.demoJWT.security.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JWTAthenticationEntryPoint point;
    @Autowired
    private JWTAuthenticationFilter filter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Chỉ ADMIN mới được truy cập đường dẫn bắt đầu với /admin
                        .requestMatchers("/login", "/register").permitAll()  // Cho phép truy cập không cần đăng nhập
                        .anyRequest().authenticated())  // Các yêu cầu khác phải đăng nhập
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}