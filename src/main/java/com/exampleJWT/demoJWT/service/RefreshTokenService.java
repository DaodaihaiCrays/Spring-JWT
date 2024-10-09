package com.exampleJWT.demoJWT.service;

import com.exampleJWT.demoJWT.model.RefreshToken;
import com.exampleJWT.demoJWT.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // Thời gian tồn tại của refresh token (ví dụ 7 ngày)
    private static final long REFRESH_TOKEN_DURATION_MS = 7 * 24 * 60 * 60 * 1000;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);  // Đã có ID người dùng
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
