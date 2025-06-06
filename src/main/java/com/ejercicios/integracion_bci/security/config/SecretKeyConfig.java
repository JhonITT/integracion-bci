package com.ejercicios.integracion_bci.security.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class SecretKeyConfig {

    @Value("${app.secret}")
    private String secret;

    @Bean
    public SecretKey secretKey() {
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("The secret key must be at least 32 characters long");
        }

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}