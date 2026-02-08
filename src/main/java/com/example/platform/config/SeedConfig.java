//“Seeder” i të dhënave: krijon admin-in default (dhe ndonjë user test) në start nëse nuk ekziston.
//
//Kjo të ndihmon sidomos kur DB është bosh (p.sh. pas down -v).
package com.example.platform.config;

import com.example.platform.entity.*;
import com.example.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SeedConfig {

    protected final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdmin() {
        return args -> {
            String adminEmail = "admin@email.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmri("Admin");
                admin.setAtesia("System");
                admin.setMbiemri("Root");
                admin.setNrTel("+355691234567");// format i saktë
                admin.setDatelindja(LocalDate.of(1990,1,1));
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("Admin@123")); // plotëson regex
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
