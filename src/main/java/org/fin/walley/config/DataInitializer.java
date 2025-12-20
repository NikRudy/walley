package org.fin.walley.config;


import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Role;
import org.fin.walley.repo.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class DataInitializer {


    @Bean
    public org.springframework.boot.CommandLineRunner initAdmin(AppUserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByUsername("admin").isEmpty()) {
                users.save(AppUser.builder()
                        .username("admin")
                        .passwordHash(encoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build());
            }
        };
    }
}