package com.ejercicios.integracion_bci.controller;

import com.ejercicios.integracion_bci.dto.PhoneDTO;
import com.ejercicios.integracion_bci.dto.UserRequestDTO;
import com.ejercicios.integracion_bci.dto.UserResponseDTO;
import com.ejercicios.integracion_bci.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequestDTO validRequest;
    private UserResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        validRequest = new UserRequestDTO(
                "John Doe",
                "john@example.com",
                "Password123",
                List.of(new PhoneDTO("123456789", "1", "57"))
        );

        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        String token = "jwt-token";

        expectedResponse = new UserResponseDTO(
                userId,
                "John Doe",
                "john@example.com",
                true,
                now,
                now,
                now,
                token,
                List.of(new PhoneDTO("123456789", "1", "57"))
        );
    }

    @Test
    @DisplayName("Debe crear un usuario exitosamente y retornar status 201")
    void createUser_Success() {
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(expectedResponse);

        ResponseEntity<?> response = userController.createUser(validRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("Debe retornar error 400 cuando el email ya existe")
    void createUser_EmailAlreadyExists() {
        String errorMessage = "El correo ya registrado";
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new EntityExistsException(errorMessage));

        ResponseEntity<?> response = userController.createUser(validRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("mensaje"));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("Debe retornar error 400 cuando el formato de email es inválido")
    void createUser_InvalidEmail() {
        UserRequestDTO invalidEmailRequest = new UserRequestDTO(
                "John Doe",
                "invalid-email",
                "Password123",
                List.of(new PhoneDTO("123456789", "1", "57"))
        );

        String errorMessage = "El email no cumple con el formato requerido";
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = userController.createUser(invalidEmailRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("mensaje"));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("Debe retornar error 400 cuando la contraseña es inválida")
    void createUser_InvalidPassword() {
        UserRequestDTO invalidPasswordRequest = new UserRequestDTO(
                "John Doe",
                "john@example.com",
                "weak",
                List.of(new PhoneDTO("123456789", "1", "57"))
        );

        String errorMessage = "La contraseña no cumple con el formato requerido";
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<?> response = userController.createUser(invalidPasswordRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals(errorMessage, errorResponse.get("mensaje"));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }
}