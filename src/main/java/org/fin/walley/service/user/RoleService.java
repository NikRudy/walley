package org.fin.walley.service.user;

import java.util.Set;

/**
 * Сервис для работы с ролями пользователей.
 * Используется для административных задач и инициализации данных безопасности.
 */
public interface RoleService {

    /**
     * Возвращает множество имён всех доступных ролей (ROLE_USER, ROLE_ADMIN и т.д.).
     */
    Set<String> getAllRoleNames();

    /**
     * Проверяет наличие роли с заданным именем.
     */
    boolean roleExists(String roleName);
}
