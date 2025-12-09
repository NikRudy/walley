package org.fin.walley.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Конфигурация шифрования паролей.
 * <p>
 * Определяет PasswordEncoder на основе BCrypt, который будет использоваться
 * как при регистрации/смене пароля, так и Spring Security при проверке
 * учетных данных пользователя.
 */
@Configuration
public class PasswordConfig {


    /**
     * BCryptPasswordEncoder – стандартный и рекомендованный алгоритм
     * хеширования паролей в Spring Security.
     * <p>
     * Преимущества:
     * - одностороннее хеширование (пароль нельзя восстановить из хэша);
     * - встроенная "соль" и настраиваемая сложность (strength);
     * - поддержка устаревания хэшей при увеличении вычислительной мощности.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}