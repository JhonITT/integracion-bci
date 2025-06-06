package com.ejercicios.integracion_bci.security.dto;

public record LoginRequest (
    String email,
    String password
){}