package com.ejercicios.integracion_bci.security.service;

import com.ejercicios.integracion_bci.security.entity.RefreshToken;
import com.ejercicios.integracion_bci.security.repository.RefreshTokenRepository;
import com.ejercicios.integracion_bci.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(String email) {
        int deletedCount = refreshTokenRepository.deleteByEmail(email);
        RefreshToken refreshToken = new RefreshToken();

        String jwtRefreshToken = jwtUtil.generateRefreshToken(email);

        refreshToken.setEmail(email);
        refreshToken.setExpiryDate(LocalDateTime.now().plusNanos(refreshTokenDurationMs * 1_000_000));
        refreshToken.setToken(jwtRefreshToken);

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public Optional<RefreshToken> validateRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = findByToken(token);

        if (refreshToken.isEmpty()) {
            return Optional.empty();
        }

        if (refreshToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken.get());
            return Optional.empty();
        }


        if (!jwtUtil.validateToken(token)) {
            refreshTokenRepository.delete(refreshToken.get());
            return Optional.empty();
        }

        RefreshToken tokenToUpdate = refreshToken.get();
        refreshTokenRepository.save(tokenToUpdate);

        return refreshToken;
    }

    @Transactional
    public int deleteByEmail(String email) {
        return refreshTokenRepository.deleteByEmail(email);
    }

}