package com.ejercicios.integracion_bci.security.service;

import com.ejercicios.integracion_bci.security.dto.UserCreateRequest;
import com.ejercicios.integracion_bci.security.dto.UserDTO;
import com.ejercicios.integracion_bci.security.entity.Phone;
import com.ejercicios.integracion_bci.security.entity.User;
import com.ejercicios.integracion_bci.security.repository.UserRepository;
import com.ejercicios.integracion_bci.security.util.JwtUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${user.password.regex}")
    private String passwordRegex;

    @Value("${user.email.regex}")
    private String emailRegex;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserDTO createUser(UserCreateRequest request) {

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
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActive(true);
        user.setToken(token);


        if (request.phones() != null) {
            List<Phone> phones = request.phones().stream()
                    .map(p -> new Phone(p.number(), p.cityCode(), p.countryCode(), user) )
                    .toList();
            user.setPhones(phones);
        }

        User savedUser = userRepository.save(user);

        return new UserDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.isActive(),
                savedUser.getCreated(),
                savedUser.getModified(),
                savedUser.getLastLogin(),
                savedUser.getToken(),
                savedUser.getPhones().stream()
                        .map(Phone::toDTO)
                        .toList()
        );
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con el email: " + email));
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional
    public void updateTokenByEmail(String email, String newToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con el email: " + email));

        user.setToken(newToken);
        user.setModified(LocalDateTime.now());

        userRepository.save(user);
    }
}
