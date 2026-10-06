package com.medicare.hms.config;

import com.medicare.hms.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Creates one demo account per role the first time the app starts, so the
 * login page can be tested right away. Change these passwords before a real deployment.
 */
@Component
public class DemoUserSeeder implements CommandLineRunner {

    private final AuthService authService;

    public DemoUserSeeder(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        authService.createUserIfMissing("admin@medicare.lk", "Admin@123", "admin", "System", "Admin");
        authService.createUserIfMissing("dr.silva@medicare.lk", "Doctor@123", "doctor", "Nimal", "Silva");
        authService.createUserIfMissing("staff@medicare.lk", "Staff@123", "staff", "Front", "Desk");
        authService.createUserIfMissing("patient@gmail.com", "Patient@123", "patient", "Demo", "Patient");
    }
}
