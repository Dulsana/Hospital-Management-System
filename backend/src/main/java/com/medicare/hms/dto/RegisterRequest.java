package com.medicare.hms.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
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
    private String email;
    private String password;
}
