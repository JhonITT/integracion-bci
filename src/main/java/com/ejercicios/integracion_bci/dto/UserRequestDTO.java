package com.ejercicios.integracion_bci.dto;

import java.util.List;

public record UserRequestDTO(
        String name,
        String email,
        String password,
        List<PhoneDTO> phones
) {}