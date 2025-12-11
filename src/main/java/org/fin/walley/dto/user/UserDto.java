package org.fin.walley.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;


/**
 * DTO для отображения и редактирования данных пользователя
 * в админке и "личном кабинете".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {


    private Long id;


    private String username;


    private String email;


    /**
     * Признак активного пользователя.
     */
    private boolean enabled;


    /**
     * Набор ролей пользователя (например, ROLE_USER, ROLE_ADMIN).
     * Здесь роли представлены строками, чтобы не тянуть доменную
     * сущность Role в транспортный слой.
     */
    private Set<String> roles;
}