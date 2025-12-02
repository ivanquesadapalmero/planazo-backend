package com.planazo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Column(length = 500)
    private String bio;

    @CreationTimestamp
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private Boolean active = true;

    // Método auxiliar para antes de persistir
    @PrePersist
    protected void onCreate() {
        if (active == null) {
            active = true;
        }
    }
}

