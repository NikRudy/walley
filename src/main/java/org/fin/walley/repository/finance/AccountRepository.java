package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Account;
import org.fin.walley.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Репозиторий для доступа к счетам пользователя.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * Все счета указанного пользователя.
     */
    List<Account> findByUser(User user);


    /**
     * Все активные счета пользователя (active = true).
     * Удобно для отображения в форме выбора счёта.
     */
    List<Account> findByUserAndActiveTrue(User user);


    /**
     * Поиск счёта пользователя по имени (например, "Наличные").
     */
    Optional<Account> findByUserAndName(User user, String name);
}