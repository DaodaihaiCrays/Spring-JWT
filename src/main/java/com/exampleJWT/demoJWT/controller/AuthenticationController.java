package com.exampleJWT.demoJWT.controller;

import com.exampleJWT.demoJWT.model.AuthenticationRequest;
import com.exampleJWT.demoJWT.ultil.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String createToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new Exception("Incorrect username or password", e);
        }

        // Giả sử chúng ta có số điện thoại, email, và vai trò của người dùng từ cơ sở dữ liệu hoặc request
        String phoneNumber = "123456789";  // Dữ liệu giả
        String email = "user@example.com";  // Dữ liệu giả
        String role = "USER";  // Dữ liệu giả
        System.out.println("hiiii");
        // Tạo token với username, phone_number, email và role
        return jwtUtil.generateToken(authenticationRequest.getUsername(), phoneNumber, email, role);
    }
}
