// File: src/main/java/org/fin/walley/dto/user/UserRegistrationDto.java
package org.fin.walley.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO для регистрации нового пользователя.
 * <p>
 * Валидация на уровне DTO отражает требования UI/формы:
 * - обязательность логина, email и пароля;
 * - минимальная длина пароля;
 * - корректный формат email.
 * Сравнение password и confirmPassword выполняется на уровне
 * бизнес-логики (сервис регистрации) или через кастомный
 * class-level валидатор.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {


    @NotBlank(message = "{validation.user.username.notBlank}")
    @Size(min = 3, max = 50, message = "{validation.user.username.size}")
    private String username;


    @NotBlank(message = "{validation.user.email.notBlank}")
    @Email(message = "{validation.user.email.format}")
    @Size(max = 100, message = "{validation.user.email.size}")
    private String email;


    @NotBlank(message = "{validation.user.password.notBlank}")
    @Size(min = 8, max = 72, message = "{validation.user.password.size}")
    private String password;


    @NotBlank(message = "{validation.user.confirmPassword.notBlank}")
    private String confirmPassword;
}