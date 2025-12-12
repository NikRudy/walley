package org.fin.walley.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * DTO для смены пароля пользователем.
 * Проверка совпадения newPassword/confirmNewPassword будет реализована
 * на бизнес-уровне или через кастомный валидатор.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDto {


    @NotBlank(message = "{user.password.current.not-blank}")
    private String currentPassword;


    @NotBlank(message = "{user.password.new.not-blank}")
    @Size(min = 8, max = 100, message = "{user.password.new.size}")
    private String newPassword;


    @NotBlank(message = "{user.password.confirm.not-blank}")
    @Size(min = 8, max = 100, message = "{user.password.confirm.size}")
    private String confirmNewPassword;
}