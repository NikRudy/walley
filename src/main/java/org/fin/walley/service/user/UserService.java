package org.fin.walley.service.user;

import org.fin.walley.dto.user.ChangePasswordDto;
import org.fin.walley.dto.user.UserDto;
import org.fin.walley.dto.user.UserRegistrationDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для управления пользователями приложения.
 * Работает с DTO и инкапсулирует бизнес-операции, связанные с User.
 */
public interface UserService {

    /**
     * Регистрация нового пользователя.
     * Проверка уникальности логина/email и шифрование пароля
     * относятся к реализации сервиса.
     */
    UserDto register(UserRegistrationDto registrationDto);

    /**
     * Поиск пользователя по идентификатору.
     */
    Optional<UserDto> findById(Long id);

    /**
     * Поиск пользователя по имени.
     */
    Optional<UserDto> findByUsername(String username);

    /**
     * Поиск пользователя по email.
     */
    Optional<UserDto> findByEmail(String email);

    /**
     * Возвращает всех пользователей (для административных интерфейсов).
     */
    List<UserDto> findAll();

    /**
     * Возвращает пользователей по имени роли (ROLE_USER, ROLE_ADMIN и т.п.).
     */
    List<UserDto> findByRole(String roleName);

    /**
     * Обновление данных профиля текущего пользователя или выбранного пользователя
     * (в админском сценарии).
     */
    UserDto updateProfile(Long userId, UserDto updatedProfile);

    /**
     * Смена пароля пользователем. Реализация должна проверять текущий пароль,
     * совпадение нового и подтверждения, а также выполнить шифрование.
     */
    void changePassword(Long userId, ChangePasswordDto changePasswordDto);

    /**
     * Блокировка пользователя (для администратора).
     */
    void disableUser(Long userId);

    /**
     * Разблокировка пользователя (для администратора).
     */
    void enableUser(Long userId);
}