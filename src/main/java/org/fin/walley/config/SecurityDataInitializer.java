
package org.fin.walley.config;

import org.fin.walley.domain.user.Role;
import org.fin.walley.domain.user.User;
import org.fin.walley.repository.user.RoleRepository;
import org.fin.walley.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Конфигурация начальной инициализации данных безопасности.
 * <p>
 * Выполняется один раз при старте приложения (через CommandLineRunner)
 * и предназначена преимущественно для среды разработки и тестирования.
 * В боевой среде может быть отключена через настройку
 * walley.security.bootstrap.enabled=false.
 */
@Configuration
public class SecurityDataInitializer {

    // Флажок включения/отключения загрузчика (по умолчанию true).
    @Value("${walley.security.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    // Параметры администратора по умолчанию (могут быть переопределены).
    @Value("${walley.security.bootstrap.admin.username:admin}")
    private String adminUsername;

    @Value("${walley.security.bootstrap.admin.email:admin@walley.local}")
    private String adminEmail;

    @Value("${walley.security.bootstrap.admin.password:admin123}")
    private String adminPassword;

    /**
     * CommandLineRunner, выполняющий инициализацию ролей и админа.
     * <p>
     * Аннотирован @ConditionalOnProperty для того, чтобы в боевой
     * конфигурации можно было полностью отключить загрузку начальных
     * данных, не меняя код.
     */
    @Bean
    @ConditionalOnProperty(prefix = "walley.security.bootstrap", name = "enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner securityBootstrapRunner(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> initializeSecurityData(roleRepository, userRepository, passwordEncoder);
    }

    @Transactional
    protected void initializeSecurityData(RoleRepository roleRepository,
                                          UserRepository userRepository,
                                          PasswordEncoder passwordEncoder) {

        if (!bootstrapEnabled) {
            // Дополнительная защита: если флаг отключен – просто выходим.
            return;
        }

        // -----------------------------------------
        // 1. Создание ролей ROLE_USER и ROLE_ADMIN
        // -----------------------------------------

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    return roleRepository.save(role);
                });

        // -----------------------------------------
        // 2. Создание администратора по умолчанию
        // -----------------------------------------

        boolean adminExists = userRepository.existsByUsername(adminUsername)
                || userRepository.existsByEmail(adminEmail);

        if (!adminExists) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEnabled(true);

            // Назначаем роли: базовую пользовательскую и административную
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }
}
