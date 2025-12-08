package org.fin.walley.repository.user;


import org.fin.walley.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий для работы с пользователями системы.
 * <p>
 * На этом этапе содержит только базовый интерфейс.
 * Специализированные методы поиска будут добавлены
 * на следующем шаге (задание 5).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
// Дополнительные методы поиска (по username, email, роли и статусу)
// будут добавлены на следующем подэтапе.
}





