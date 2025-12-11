package org.fin.walley.service.finance;


import org.fin.walley.dto.finance.TransactionDto;
import org.fin.walley.domain.finance.TransactionType;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * Сервис управления финансовыми транзакциями.
 */
public interface TransactionService {


    /**
     * Создание новой транзакции для пользователя.
     */
    TransactionDto createTransaction(Long userId, TransactionDto transactionDto);


    /**
     * Обновление существующей транзакции.
     */
    TransactionDto updateTransaction(Long userId, Long transactionId, TransactionDto transactionDto);


    /**
     * Логическое удаление транзакции (soft delete).
     */
    void softDeleteTransaction(Long userId, Long transactionId);


    /**
     * Физическое удаление транзакции (если разрешено политикой).
     */
    void hardDeleteTransaction(Long userId, Long transactionId);


    Optional<TransactionDto> getTransactionById(Long userId, Long transactionId);


    /**
     * Получение списка транзакций по фильтрам: период, тип, категории, счёта.
     */
    List<TransactionDto> getTransactions(Long userId,
                                         LocalDateTime from,
                                         LocalDateTime to,
                                         TransactionType type,
                                         Long accountId,
                                         Long categoryId,
                                         Long subcategoryId,
                                         boolean includeDeleted);
}