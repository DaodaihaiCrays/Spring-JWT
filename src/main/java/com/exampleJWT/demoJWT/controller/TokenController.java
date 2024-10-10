package com.exampleJWT.demoJWT.controller;

import com.exampleJWT.demoJWT.model.JwtResponse;
import com.exampleJWT.demoJWT.model.RefreshToken;
import com.exampleJWT.demoJWT.service.CustomUserDetailsService;
import com.exampleJWT.demoJWT.service.RefreshTokenService;
import com.exampleJWT.demoJWT.security.JWTHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class TokenController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {

        return refreshTokenService.findByToken(refreshToken)
                .map(token -> {
                    System.out.println(token);
                    System.out.println("***********");
                    if (refreshTokenService.isRefreshTokenExpired(token)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token is expired!");
                    }
                    // Lấy thông tin người dùng từ token
                    Long userId = token.getUser().getId();  // Lấy userId từ RefreshToken
                    String email = token.getUser().getEmail();

                    // Lấy UserDetails từ email
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // Tạo Access Token mới từ thông tin userDetails và userId
                    String newAccessToken = jwtHelper.generateToken(userDetails, userId);

                    // Trả về response chứa Access Token mới
                    JwtResponse response = JwtResponse.builder()
                            .jwtToken(newAccessToken)
                            .refreshToken(refreshToken)
                            .username(email)
                            .build();

                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh token not found!"));
    }

}
