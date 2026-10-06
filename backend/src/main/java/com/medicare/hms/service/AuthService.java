package com.medicare.hms.service;

import com.medicare.hms.util.PhoneValidator;
import com.medicare.hms.dto.AuthResponse;
import com.medicare.hms.dto.LoginRequest;
import com.medicare.hms.dto.RegisterRequest;
import com.medicare.hms.model.User;
import com.medicare.hms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest req) {
        if (isBlank(req.getEmail()) || isBlank(req.getPassword()) || isBlank(req.getFirstName())) {
            throw new IllegalArgumentException("First name, email and password are required.");
        }
        if (req.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        String phone = PhoneValidator.requireValid(req.getPhone(), "Phone number");
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("An account with this email already exists.");
        }

        // Public sign-up always creates a patient account. Doctor/admin/staff
        // accounts are created by an administrator, never from the sign-up page.
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role("patient")
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName())
                .nicOrPassport(req.getNicOrPassport())
                .dateOfBirth(req.getDateOfBirth())
                .bloodGroup(req.getBloodGroup())
                .gender(req.getGender())
                .phone(phone)
                .address(req.getAddress())
                .city(req.getCity())
                .district(req.getDistrict())
                .emergencyContact(req.getEmergencyContact())
                .build();
        return toResponse(userRepository.save(user));
    }

    public AuthResponse login(LoginRequest req) {
        if (isBlank(req.getEmail()) || isBlank(req.getPassword())) {
            throw new IllegalArgumentException("Please enter your email and password.");
        }
        // Same message for unknown email and wrong password, so attackers
        // cannot find out which emails are registered.
        User user = userRepository.findByEmailIgnoreCase(req.getEmail().trim())
                .filter(u -> passwordEncoder.matches(req.getPassword(), u.getPasswordHash()))
                .orElseThrow(() -> new SecurityException("Invalid email or password."));

        if (!user.isActive()) {
            throw new SecurityException("This account has been deactivated. Please contact the hospital.");
        }
        if (!isBlank(req.getRole()) && !req.getRole().equalsIgnoreCase(user.getRole())) {
            throw new SecurityException("This account is not registered as " + req.getRole()
                    + ". Please select the correct role.");
        }
        return toResponse(user);
    }

    public void createUserIfMissing(String email, String rawPassword, String role, String firstName, String lastName) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .build());
    }

    private AuthResponse toResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
