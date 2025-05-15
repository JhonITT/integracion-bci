package com.ejercicios.integracion_bci.service;

import com.ejercicios.integracion_bci.dto.PhoneDTO;
import com.ejercicios.integracion_bci.dto.UserRequestDTO;
import com.ejercicios.integracion_bci.dto.UserResponseDTO;
import com.ejercicios.integracion_bci.entity.Phone;
import com.ejercicios.integracion_bci.entity.User;
import com.ejercicios.integracion_bci.repository.UserRepository;
import com.ejercicios.integracion_bci.util.JwtUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${user.password.regex}")
    private String passwordRegex;

    @Value("${user.email.regex}")
    private String emailRegex;


    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EntityExistsException("El correo ya registrado");
        }

        if (!request.email().matches(emailRegex)) {
            throw new IllegalArgumentException("El email no cumple con el formato requerido");
        }

        if (!request.password().matches(passwordRegex)) {
            throw new IllegalArgumentException("La contraseña no cumple con el formato requerido");
        }

        String token = jwtUtil.generateAccessToken(request.email());

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password()); // Como esto es un MVP no me preocupe por cifrar esto
        user.setActive(true);
        user.setToken(token);


        if (request.phones() != null) {
            List<Phone> phones = request.phones().stream()
                    .map(p -> new Phone(p.number(), p.cityCode(), p.countryCode(), user) )
                    .toList();
            user.setPhones(phones);
        }

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.isActive(),
                savedUser.getCreated(),
                savedUser.getModified(),
                savedUser.getLastLogin(),
                savedUser.getToken(),
                savedUser.getPhones().stream()
                        .map(p -> new PhoneDTO(p.getNumber(), p.getCityCode(), p.getCountryCode()))
                        .toList()
        );
    }
}
