package com.exampleJWT.demoJWT.config;

import com.exampleJWT.demoJWT.model.JwtRequest;
import com.exampleJWT.demoJWT.model.JwtResponse;
import com.exampleJWT.demoJWT.model.RefreshToken;
import com.exampleJWT.demoJWT.model.User;
import com.exampleJWT.demoJWT.security.JWTHelper;
import com.exampleJWT.demoJWT.service.CustomUserDetailsService;
import com.exampleJWT.demoJWT.service.RefreshTokenService;
import com.exampleJWT.demoJWT.service.UserRepository;
import com.exampleJWT.demoJWT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JWTHelper helper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        // Xác thực thông tin đăng nhập
        this.doAuthenticate(request.getEmail(), request.getPassword());

        // Lấy thông tin người dùng sau khi xác thực thành công
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Lấy thông tin ID của người dùng từ cơ sở dữ liệu
        User user = userService.getUserByEmail(request.getEmail()).orElseThrow();

        // Tạo Access Token với ID người dùng
        String accessToken = this.helper.generateToken(userDetails, user.getId());

        // Tạo Refresh Token và lưu vào cơ sở dữ liệu với ID người dùng
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // Tạo JwtResponse để trả về Access Token và Refresh Token
        JwtResponse response = JwtResponse.builder()
                .jwtToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Nếu không có vai trò được chỉ định, mặc định là USER
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        userService.saveUser(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password!!");
        }
    }
}
