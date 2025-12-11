package org.fin.walley.service.user;


import org.fin.walley.dto.user.ChangePasswordDto;
import org.fin.walley.dto.user.UserDto;
import org.fin.walley.dto.user.UserRegistrationDto;


import java.util.List;
import java.util.Optional;


/**
 * Сервис управления пользователями.
 * <p>
 * Инкапсулирует операции регистрации, работы с профилем и
 * административные действия (блокировка/разблокировка, назначение ролей).
 */
public interface UserService {


    /**
     * Регистрация нового пользователя на основе DTO с данными формы.
     * <p>
     * Реализация должна:
     * - проверить уникальность username и email;
     * - зашифровать пароль;
     * - назначить роль по умолчанию (ROLE_USER);
     * - вернуть представление UserDto для дальнейшего использования в UI.
     */
    UserDto registerUser(UserRegistrationDto registrationDto);


    /**
     * Получение пользователя по идентификатору.
     */
    Optional<UserDto> findById(Long id);


    /**
     * Поиск пользователя по username.
     */
    Optional<UserDto> findByUsername(String username);


    /**
     * Поиск пользователя по email.
     */
    Optional<UserDto> findByEmail(String email);


    /**
     * Изменение базовых данных профиля (username, email и т.п.).
     * <p>
     * Реализация должна учитывать ограничения безопасности
     * (например, кто имеет право менять какие данные).
     */
    UserDto updateUserProfile(Long userId, UserDto updatedData);


    /**
     * Смена пароля пользователем.
     * <p>
     * Реализация должна:
     * - проверить текущий пароль;
     * - убедиться, что новый пароль и подтверждение совпадают;
     * - сохранить новый пароль в зашифрованном виде.
     */
    void changePassword(Long userId, ChangePasswordDto changePasswordDto);


    /**
     * Блокировка пользователя (административная операция).
     */
    void disableUser(Long userId);


    /**
     * Разблокировка пользователя (административная операция).
     */
    void enableUser(Long userId);


    /**
     * Получение всех пользователей (для админ-интерфейса).
     */
    List<UserDto> findAll();

