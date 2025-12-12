package org.fin.walley.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.util.Set;


/**
 * DTO-представление пользователя для профиля и административного интерфейса.
 * Здесь валидация минимальна, т.к. часть проверок (уникальность email/логина)
 * выполняется на бизнес-уровне в сервисах.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {


    @NotNull(message = "{user.id.not-null}")
    private Long id;


    @NotBlank(message = "{user.username.not-blank}")
    @Size(min = 3, max = 50, message = "{user.username.size}")
    private String username;


    @NotBlank(message = "{user.email.not-blank}")
    @Email(message = "{user.email.invalid}")
    private String email;


    private boolean enabled;


    /**
     * Набор имён ролей (например, ROLE_USER, ROLE_ADMIN).
     * Здесь валидация будет чаще проверяться на бизнес-уровне
     * (например, что хотя бы одна роль присутствует).
     */
    private java.util.Set<String> roles;
}