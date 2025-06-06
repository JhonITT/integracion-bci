package com.ejercicios.integracion_bci.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PhoneDTO(
        Long id,
        String number,
        @JsonProperty("citycode")
        String cityCode,
        @JsonProperty("contrycode")
        String countryCode
) {}