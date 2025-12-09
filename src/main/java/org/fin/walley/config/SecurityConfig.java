package org.fin.walley.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Основная конфигурация Spring Security для приложения Walley.
 *
 * Выполняет:
 *  - разграничение доступа по ролям;
 *  - настройку формы логина и выхода;
 *  - обработку отказа в доступе (403).
 *
 * CSRF-защита остаётся включённой по умолчанию (стандартное поведение
 * Spring Security); для Thymeleaf-форм нужно выводить CSRF-токен.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // -----------------------------
        // 1. Правила доступа к URL
        // -----------------------------
        http.authorizeHttpRequests(auth -> auth
                // Общедоступные ресурсы (статические файлы, логин, регистрация)
                .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/",
                        "/login",
                        "/register",
                        "/error"
                ).permitAll()

                // Админ-раздел – только ROLE_ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // Пользовательские разделы – ROLE_USER и выше
                .requestMatchers(
                        "/app/**",
                        "/accounts/**",
                        "/transactions/**",
                        "/categories/**",
                        "/import/**",
                        "/export/**"
                ).hasRole("USER")

                // Всё остальное – только для аутентифицированных
                .anyRequest().authenticated()
        );

        // -----------------------------
        // 2. Форма логина
        // -----------------------------
        http.formLogin(form -> form
                // Кастомная страница логина (GET /login рендерит Thymeleaf-шаблон)
                .loginPage("/login")
                // URL для POST с логином/паролем (th:action должен вести сюда)
                .loginProcessingUrl("/login")
                // Куда перенаправлять после успешного логина
                .defaultSuccessUrl("/app/dashboard", true)
                // Куда перенаправлять при ошибке
                .failureUrl("/login?error=true")
                .permitAll()
        );

        // -----------------------------
        // 3. Logout
        // -----------------------------
        http.logout(logout -> logout
                // URL для выхода (форма/ссылка должна указывать сюда)
                .logoutUrl("/logout")
                // Куда перенаправлять после выхода
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        // -----------------------------
        // 4. Обработка отказа в доступе (403)
        // -----------------------------
        http.exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
        );

        // -----------------------------
        // 5. CSRF
        // -----------------------------
        // CSRF уже включён по умолчанию.
        // Для Thymeleaf-форм нужно добавлять токен, например:
        //
        // <input type="hidden"
        //        th:name="${_csrf.parameterName}"
        //        th:value="${_csrf.token}"/>
        //
        // Отдельная явная настройка http.csrf(...) здесь не требуется.

        return http.build();
    }
}
