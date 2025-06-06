package com.ejercicios.integracion_bci.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String email,
        @JsonProperty("isactive")
        boolean active,
        LocalDateTime created,
        LocalDateTime modified,
        LocalDateTime lastLogin,
        String token,
        List<PhoneDTO> phones
) {}