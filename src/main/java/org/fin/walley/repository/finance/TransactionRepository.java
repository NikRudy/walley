package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий для финансовых операций (транзакций).
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
// Методы для выборки по пользователю, интервалу дат, типу,
// категории и счёту будут добавлены на следующем шаге.
}