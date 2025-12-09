package org.fin.walley.service.user;


import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.user.Role;
import org.fin.walley.domain.user.User;
import org.fin.walley.repository.user.RoleRepository;
import org.fin.walley.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Сервис регистрации пользователей.
 * <p>
 * Демонстрирует, как обеспечить сохранение паролей исключительно
 * в зашифрованном виде, используя PasswordEncoder (BCrypt).
 */
@Service
@RequiredArgsConstructor
public class UserRegistrationService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * DTO-регистрации. В реальном коде этот класс обычно выносится
     * в пакет с DTO (например, org.fin.walley.web.dto.user.RegisterRequest).
     * Здесь он приведён как вложенный класс для компактности примера.
     */
    public record RegisterRequest(String username, String email, String password) {
    }


    /**
     * Регистрация нового пользователя.
     * <p>
     * ВАЖНО: пароль никогда не сохраняется в открытом виде – только хэш
     * через passwordEncoder.encode(...).
     */
    @Transactional
    public User registerNewUser(RegisterRequest request) {
// 1. Валидация уникальности username/email (минимальный пример)
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already in use: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use: " + request.email());
        }


// 2. Получаем роль по умолчанию (ROLE_USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER is not configured"));


// 3. Хешируем пароль перед сохранением
        String encodedPassword = passwordEncoder.encode(request.password());


// 4. Создаём доменный объект User
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(encodedPassword); // Сохраняем только хэш!
        user.setEnabled(true);
        user.getRoles().add(userRole);


// 5. Сохраняем пользователя в БД
        return userRepository.save(user);
    }
}