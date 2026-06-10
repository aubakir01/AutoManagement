package com.example.userlistapp;

import com.example.userlistapp.model.AppUser;
import com.example.userlistapp.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Создаём admin если не существует
        if (appUserRepository.findByUsername("admin").isEmpty()) {
            appUserRepository.save(new AppUser(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN"
            ));
            System.out.println("✅ Создан пользователь: admin / admin123");
        }

        // Создаём user если не существует
        if (appUserRepository.findByUsername("user").isEmpty()) {
            appUserRepository.save(new AppUser(
                    "user",
                    passwordEncoder.encode("user123"),
                    "ROLE_USER"
            ));
            System.out.println("✅ Создан пользователь: user / user123");
        }
    }
}