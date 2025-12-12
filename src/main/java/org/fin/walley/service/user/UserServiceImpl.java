package org.fin.walley.service.user;

import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.user.Role;
import org.fin.walley.domain.user.User;
import org.fin.walley.dto.user.ChangePasswordDto;
import org.fin.walley.dto.user.UserDto;
import org.fin.walley.dto.user.UserRegistrationDto;
import org.fin.walley.repository.user.RoleRepository;
import org.fin.walley.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления пользователями.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserRegistrationDto registrationDto) {
        // Проверка уникальности логина и email
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already in use: " + registrationDto.getUsername());
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + registrationDto.getEmail());
        }

        // Проверка совпадения паролей (бизнес-логика, не Bean Validation)
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirmation do not match");
        }

        // Назначаем базовую роль ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found"));

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(userRole);

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        return role.getUsers().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateProfile(Long userId, UserDto updatedProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Проверка уникальности username/email при изменении
        if (!user.getUsername().equals(updatedProfile.getUsername()) &&
                userRepository.existsByUsername(updatedProfile.getUsername())) {
            throw new IllegalArgumentException("Username already in use: " + updatedProfile.getUsername());
        }

        if (!user.getEmail().equals(updatedProfile.getEmail()) &&
                userRepository.existsByEmail(updatedProfile.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + updatedProfile.getEmail());
        }

        user.setUsername(updatedProfile.getUsername());
        user.setEmail(updatedProfile.getEmail());

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Проверка текущего пароля
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Проверка совпадения нового пароля и подтверждения
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setEnabled(true);
        userRepository.save(user);
    }

    private UserDto toDto(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(roleNames)
                .build();
    }
}
