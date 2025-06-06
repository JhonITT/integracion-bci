package com.ejercicios.integracion_bci.security.dto;

import java.util.List;

public record UserCreateRequest(
        String name,
        String email,
        String password,
        List<PhoneDTO> phones
) {}