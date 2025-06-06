package com.ejercicios.integracion_bci.security.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

// Un ratelimit para el login, tambien iba a agregar un cache de segunda clase como caffeine pero hoy es viernes :)
@Component
public class AccessApiRateLimitFilter implements Filter {

    private final Bucket bucket = AccessApiRateLimitFilter.createBucket();

    public static Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("/login".equals(httpRequest.getRequestURI()) && !bucket.tryConsume(1)) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("Too many login attempts. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

}