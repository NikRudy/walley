package org.fin.walley.repository.user;

import org.fin.walley.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с ролями безопасности.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Поиск роли по её имени (например, "ROLE_USER").
     */
    Optional<Role> findByName(String name);
}


