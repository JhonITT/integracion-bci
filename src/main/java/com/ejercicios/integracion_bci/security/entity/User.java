package com.ejercicios.integracion_bci.security.entity;

import com.ejercicios.integracion_bci.security.dto.PhoneDTO;
import com.ejercicios.integracion_bci.security.dto.UserDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_app")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(name = "modified", nullable = false)
    private LocalDateTime modified;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // No veo el sentido de esto, imagino que es para que el usuario desde que se cree
    // se le haga login
    @Column(name = "token")
    private String token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.created = now;
        this.lastLogin = now;
        this.modified = now;
    }

    @PreUpdate
    protected void onUpdate() {
        LocalDateTime.now();
    }

    public UserDTO toDTO() {
        List<PhoneDTO> phoneDTOs = this.getPhones().stream()
                .map(Phone::toDTO)
                .toList();

        return new UserDTO(
                this.getId(),
                this.getName(),
                this.getEmail(),
                this.isActive(),
                this.getCreated(),
                this.getModified(),
                this.getLastLogin(),
                this.getToken(),
                phoneDTOs
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}