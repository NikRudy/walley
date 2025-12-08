package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий для доступа к счетам пользователя.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
// Методы поиска по пользователю и признаку активности
// будут добавлены на следующем подэтапе.
}