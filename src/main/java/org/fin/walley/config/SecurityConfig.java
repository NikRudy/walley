package org.fin.walley.config;


import org.fin.walley.domain.AppUser;
import org.fin.walley.repo.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService(AppUserRepository users) {
        return username -> {
            AppUser u = users.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));


            return User.withUsername(u.getUsername())
                    .password(u.getPasswordHash())
                    .roles(u.getRole().name())
                    .disabled(!u.isEnabled())
                    .build();
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/register").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/transactions", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
// Для Postman/JSON API проще отключить CSRF только на /api/**
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));


        return http.build();
    }
}