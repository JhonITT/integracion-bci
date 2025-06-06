package com.ejercicios.integracion_bci.security.controller;

import com.ejercicios.integracion_bci.security.dto.UserCreateRequest;
import com.ejercicios.integracion_bci.security.dto.UserDTO;
import com.ejercicios.integracion_bci.security.entity.User;
import com.ejercicios.integracion_bci.security.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequest request) {
        try {
            UserDTO response = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityExistsException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.CREATED).body(users.stream().map(User::toDTO).toList());
    }

}