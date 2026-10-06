package com.medicare.hms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // BCrypt hash only - the plain password is never stored
    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    // patient, doctor, admin, staff
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String firstName;

    private String lastName;
    private String nicOrPassport;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String gender;
    private String phone;
    private String address;
    private String city;
    private String district;
    private String emergencyContact;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
