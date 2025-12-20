package org.fin.walley.service;


import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Role;
import org.fin.walley.repo.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class UserService {


    private final AppUserRepository users;
    private final PasswordEncoder encoder;


    public UserService(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }


    public boolean usernameTaken(String username) {
        return users.existsByUsername(username);
    }


    @Transactional
    public AppUser registerUser(String username, String rawPassword) {
        if (users.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }


        AppUser u = AppUser.builder()
                .username(username)
                .passwordHash(encoder.encode(rawPassword))
                .role(Role.USER)
                .enabled(true)
                .build();


        return users.save(u);
    }


    // ADMIN
    public List<AppUser> findAll() {
        return users.findAll();
    }


    public AppUser findById(Long id) {
        return users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    @Transactional
    public AppUser createByAdmin(String username, String rawPassword, Role role, boolean enabled) {
        if (users.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        return users.save(AppUser.builder()
                .username(username)
                .passwordHash(encoder.encode(rawPassword))
                .role(role)
                .enabled(enabled)
                .build());
    }


    @Transactional
    public AppUser updateByAdmin(Long id, String rawPasswordOrNull, Role role, boolean enabled) {
        AppUser u = findById(id);
        if (rawPasswordOrNull != null && !rawPasswordOrNull.isBlank()) {
            u.setPasswordHash(encoder.encode(rawPasswordOrNull));
        }
        u.setRole(role);
        u.setEnabled(enabled);
        return u;
    }


    @Transactional
    public void delete(Long id) {
        users.deleteById(id);
    }
}