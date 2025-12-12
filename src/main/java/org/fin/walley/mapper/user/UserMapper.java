package org.fin.walley.mapper.user;

import org.fin.walley.domain.user.Role;
import org.fin.walley.domain.user.User;
import org.fin.walley.dto.user.UserDto;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для маппинга между User и UserDto.
 */
public final class UserMapper {

    private UserMapper() {
        // utility class
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(roleNames)
                .build();
    }

    /**
     * Обновляет поля пользователя на основе UserDto (без пароля и ролей).
     * Логика валидации (уникальность логина/email) остаётся в сервисе.
     */
    public static void updateUserFromDto(UserDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // enabled и роли управляются бизнес-логикой (админские сценарии), поэтому здесь не трогаем
    }
}
