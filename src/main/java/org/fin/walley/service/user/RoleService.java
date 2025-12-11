package org.fin.walley.service.user;


import java.util.List;


/**
 * Сервис управления ролями.
 * <p>
 * В небольших системах роли чаще всего статичны, однако
 * в рамках архитектуры выделение RoleService облегчает
 * дальнейшее развитие (добавление новых ролей, описание
 * их метаданных и т.п.).
 */
public interface RoleService {


    /**
     * Получить список всех доступных ролей (в виде строковых имён,
     * например, "ROLE_USER", "ROLE_ADMIN").
     */
    List<String> getAllRoleNames();


    /**
     * Проверить существование роли.
     */
    boolean roleExists(String roleName);
}