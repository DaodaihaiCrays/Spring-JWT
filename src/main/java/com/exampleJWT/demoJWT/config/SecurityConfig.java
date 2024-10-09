package com.exampleJWT.demoJWT.config;

import com.exampleJWT.demoJWT.security.JWTAthenticationEntryPoint;
import com.exampleJWT.demoJWT.security.JWTAuthenticationFilter;
import com.exampleJWT.demoJWT.security.JWTHelper;
import com.exampleJWT.demoJWT.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;


// Cách 1 này thì phải thêm @Component bên class JWTAuthenticationFilter
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
                        .requestMatchers("/login", "/register").permitAll()  // Cho phép truy cập mà không cần xác thực
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // Chỉ ADMIN mới được truy cập
                        .anyRequest().authenticated())  // Các yêu cầu khác phải đăng nhập
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Thêm bộ lọc JWT nhưng chỉ áp dụng cho những endpoint cần xác thực
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


