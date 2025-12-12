package org.fin.walley.service.finance;

import org.fin.walley.domain.finance.TransactionType;
import org.fin.walley.dto.finance.TransactionDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления финансовыми транзакциями пользователя.
 */
public interface TransactionService {

    /**
     * Создание новой транзакции для пользователя.
     */
    TransactionDto createTransaction(Long userId, TransactionDto transactionDto);

    /**
     * Обновление существующей транзакции пользователя.
     */
    TransactionDto updateTransaction(Long userId, Long transactionId, TransactionDto transactionDto);

    /**
     * Логическое удаление транзакции (пометка как deleted=true).
     */
    void softDeleteTransaction(Long userId, Long transactionId);

    /**
     * Полное удаление транзакции из системы.
     */
    void hardDeleteTransaction(Long userId, Long transactionId);

    /**
     * Получение транзакции по идентификатору для конкретного пользователя.
     */
    Optional<TransactionDto> getTransactionById(Long userId, Long transactionId);

    /**
     * Фильтрация транзакций по периоду, типу, счёту, категории и подкатегории.
     * Параметры фильтра могут быть null (означает отсутствие фильтра).
     */
    List<TransactionDto> getTransactions(
            Long userId,
            LocalDateTime from,
            LocalDateTime to,
            TransactionType type,
            Long accountId,
            Long categoryId,
            Long subcategoryId,
            boolean includeDeleted
    );
}
