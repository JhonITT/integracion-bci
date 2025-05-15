package com.ejercicios.integracion_bci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PhoneDTO(
        String number,
        @JsonProperty("citycode")
        String cityCode,
        @JsonProperty("contrycode")
        String countryCode
) {}