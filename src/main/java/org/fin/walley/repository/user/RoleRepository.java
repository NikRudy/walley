package org.fin.walley.repository.user;

import org.fin.walley.domain.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с ролями безопасности.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
// Типовые методы поиска по имени роли будут добавлены отдельно.
}


