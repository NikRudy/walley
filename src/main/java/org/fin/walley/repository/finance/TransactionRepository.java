package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Account;
import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.Transaction;
import org.fin.walley.domain.finance.TransactionType;
import org.fin.walley.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Репозиторий для финансовых операций (транзакций).
 * <p>
 * Содержит методы, покрывающие основные сценарии отображения и анализа
 * операций в UI: фильтрация по пользователю, датам, типу, категории и счёту.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * Транзакции пользователя за указанный интервал дат (включительно).
     * Можно использовать для построения отчётов за период.
     */
    List<Transaction> findByUserAndOccurredAtBetween(User user,
                                                     LocalDateTime from,
                                                     LocalDateTime to);


    /**
     * Транзакции пользователя по типу операции (доход/расход).
     */
    List<Transaction> findByUserAndType(User user, TransactionType type);


    /**
     * Транзакции пользователя по категории.
     */
    List<Transaction> findByUserAndCategory(User user, Category category);


    /**
     * Транзакции пользователя по счёту.
     */
    List<Transaction> findByUserAndAccount(User user, Account account);
}