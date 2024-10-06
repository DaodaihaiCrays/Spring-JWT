package com.exampleJWT.demoJWT.ultil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Tạo khóa bí mật an toàn cho HS256
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Phương thức trích xuất username từ token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Phương thức trích xuất thông tin khác từ token (số điện thoại, email, role)
    public String extractClaim(String token, String claimKey) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(claimKey);  // Trích xuất thông tin từ claims bằng key
    }

    // Tạo token với 4 trường: username, phone_number, email, và role
    public String generateToken(String username, String phoneNumber, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone_number", phoneNumber);  // Lưu số điện thoại vào claims
        claims.put("email", email);               // Lưu email vào claims
        claims.put("role", role);                 // Lưu vai trò (role) vào claims
        return createToken(claims, username);
    }

    // Tạo JWT với claims và subject
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)  // Đặt username làm subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token có hiệu lực trong 10 giờ
                .signWith(SECRET_KEY)  // Ký token bằng khóa bí mật
                .compact();
    }
}
