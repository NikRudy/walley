package org.fin.walley.repository.user;


import org.fin.walley.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Репозиторий для работы с пользователями системы.
 * <p>
 * На этом этапе содержит только базовый интерфейс.
 * Специализированные методы поиска будут добавлены
 * на следующем шаге (задание 5).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * * Поиск пользователя по username (логин для аутентификации).
            */
    Optional<User> findByUsername(String username);


    /**
     * Поиск пользователя по email (полезно для восстановления
     * доступа, проверки уникальности и т.п.).
     */
    Optional<User> findByEmail(String email);


    /**
     * Проверка существования пользователя с данным username.
     * Удобно для валидации формы регистрации.
     */
    boolean existsByUsername(String username);


    /**
     * Проверка существования пользователя с данным email.
     */
    boolean existsByEmail(String email);


    /**
     * Список только активных (enabled = true) пользователей.
     * Можно использовать для отображения в админке.
     */
    List<User> findByEnabledTrue();


    /**
     * Список пользователей, имеющих указанную роль.
     * roleName ожидается в формате "ROLE_USER", "ROLE_ADMIN" и т.п.
     */
    List<User> findByRoles_Name(String roleName);


    /**
     * Список активных пользователей с указанной ролью.
     * Удобно для фильтрации администраторов, обычных пользователей и т.д.
     */
    List<User> findByRoles_NameAndEnabledTrue(String roleName);
}





