package com.exampleJWT.demoJWT.ultil;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Tạo secret key an toàn và đủ mạnh tự động cho HS256
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Phương thức tạo token
    public String generateToken(String username, String phoneNumber, String email, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("phone_number", phoneNumber)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Token có hiệu lực trong 10 giờ
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Phương thức xác thực và trích xuất username từ token
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            // Xử lý tất cả các ngoại lệ của JWT như: ExpiredJwtException, MalformedJwtException, SignatureException
            System.out.println("Token không hợp lệ: " + e.getMessage());
            return null;
        }
    }

    // Phương thức kiểm tra token hợp lệ
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername != null && extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Kiểm tra token có hết hạn không
    private Boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
