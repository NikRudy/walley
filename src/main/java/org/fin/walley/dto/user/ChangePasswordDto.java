package org.fin.walley.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO для смены пароля пользователем.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDto {


    /**
     * Текущий пароль пользователя (для верификации).
     */
    private String currentPassword;


    /**
     * Новый пароль.
     */
    private String newPassword;


    /**
     * Подтверждение нового пароля.
     */
    private String confirmNewPassword;
}