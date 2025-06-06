package com.ejercicios.integracion_bci.security.controller;

import com.ejercicios.integracion_bci.security.entity.RefreshToken;
import com.ejercicios.integracion_bci.security.entity.User;
import com.ejercicios.integracion_bci.security.service.RefreshTokenService;
import com.ejercicios.integracion_bci.security.service.UserService;
import com.ejercicios.integracion_bci.security.util.JwtUtil;
import com.ejercicios.integracion_bci.security.dto.LoginRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          RefreshTokenService refreshTokenService,
                          UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Solo para capturar el BadCredentialsException
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            String accessToken = jwtUtil.generateAccessToken(loginRequest.email());
            userService.updateTokenByEmail(loginRequest.email(), accessToken);

            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(loginRequest.email());
            String refreshToken = refreshTokenEntity.getToken();

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("email", loginRequest.email());
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);
            responseBody.put("authenticated", true);

            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cuenta deshabilitada. Contacte con el administrador");
        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cuenta bloqueada. Contacte con soporte");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error durante el inicio de sesión. Intente nuevamente más tarde");
        }
    }

    @GetMapping("/verify-token")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");

            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado");
            }

            String accessToken = bearerToken.substring(7);
            String email = jwtUtil.getEmailFromToken(accessToken);
            User user = userService.findByEmail(email);

            if (!jwtUtil.validateToken(accessToken) || user.getToken() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);

            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al verificar el token");
        }
    }

    @PostMapping("/refresh-token")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> requestMap) {
        try {
            String refreshTokenStr = requestMap.get("refreshToken");

            if (refreshTokenStr == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token no proporcionado");
            }

            Optional<RefreshToken> refreshTokenOpt = refreshTokenService.validateRefreshToken(refreshTokenStr);

            if (refreshTokenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token inválido o expirado");
            }

            RefreshToken refreshTokenEntity = refreshTokenOpt.get();
            String email = refreshTokenEntity.getEmail();

            String newAccessToken = jwtUtil.generateAccessToken(email);

            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(email);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("email", email);
            responseBody.put("accessToken", newAccessToken);
            responseBody.put("refreshToken", newRefreshToken.getToken());
            responseBody.put("tokenRefreshed", true);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error al renovar el token");
        }
    }

    /*
    * Este logout lo utilizo únicamente para manejar el refresh token.
    * El access token es irrevocable y debe expirar por sí mismo; no quiero validarlo en cada solicitud.
    * Por eso opté por este diseño: si en algún momento se desea revocar un token,
    * simplemente se agrega a la base de datos y se verifica en cada petición.
    */
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String accessToken = bearerToken.substring(7);

                String email = jwtUtil.getEmailFromToken(accessToken);

                int deletedCount = refreshTokenService.deleteByEmail(email);
                userService.updateTokenByEmail(email, null);

                return ResponseEntity.ok("Sesión cerrada exitosamente");
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cerrar sesión: " + e.getMessage());
        }
    }
}