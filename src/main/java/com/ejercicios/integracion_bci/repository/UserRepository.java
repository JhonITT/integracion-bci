package com.ejercicios.integracion_bci.repository;

import com.ejercicios.integracion_bci.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);
}
