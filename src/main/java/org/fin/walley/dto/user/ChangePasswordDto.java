package org.fin.walley.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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


    @NotBlank(message = "{validation.user.currentPassword.notBlank}")
    private String currentPassword;


    @NotBlank(message = "{validation.user.newPassword.notBlank}")
    @Size(min = 8, max = 72, message = "{validation.user.newPassword.size}")
    private String newPassword;


    @NotBlank(message = "{validation.user.confirmNewPassword.notBlank}")
    private String confirmNewPassword;
}