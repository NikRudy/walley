package org.fin.walley.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO для регистрации нового пользователя.
 * Используется на уровне форм/REST-запросов и не содержит
 * технических полей (id, роли, флаги и т.п.).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDto {


    private String username;


    private String email;


    private String password;


    /**
     * Подтверждение пароля. На уровне бизнес-логики этот
     * атрибут используется только для сравнения с password,
     * в БД не сохраняется.
     */
    private String confirmPassword;
}