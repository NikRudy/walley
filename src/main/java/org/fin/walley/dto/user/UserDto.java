package org.fin.walley.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;


/**
 * DTO для отображения и редактирования данных пользователя
 * в админке и "личном кабинете".
 * <p>
 * id здесь не валидируется как @NotNull, так как может
 * отсутствовать при создании нового пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {


    private Long id;


    @NotBlank(message = "{validation.user.username.notBlank}")
    @Size(min = 3, max = 50, message = "{validation.user.username.size}")
    private String username;


    @NotBlank(message = "{validation.user.email.notBlank}")
    @Email(message = "{validation.user.email.format}")
    @Size(max = 100, message = "{validation.user.email.size}")
    private String email;


    /**
     * Признак активного пользователя.
     */
    private boolean enabled;


    /**
     * Набор ролей пользователя (например, ROLE_USER, ROLE_ADMIN).
     * На уровне DTO достаточно проверять, что коллекция не пуста,
     * более сложные правила (например, наличие как минимум ROLE_USER)
     * относятся к бизнес-логике.
     */
    private Set<String> roles;
}