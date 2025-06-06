package com.ejercicios.integracion_bci.security.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityTestController {

    @GetMapping("/on")
    @SecurityRequirement(name = "bearerAuth")
    public String securityOn() {
        return "Correct Response!!!!!";
    }

    @GetMapping("/off")
    public String securityOf() {
        return "Correct Response!!!!!";
    }

}
