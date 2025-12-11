package org.fin.walley.service.finance;


import org.fin.walley.dto.finance.AccountDto;


import java.util.List;
import java.util.Optional;


/**
 * Сервис для управления счетами пользователя.
 */
public interface AccountService {


    /**
     * Создание нового счёта для пользователя.
     */
    AccountDto createAccount(Long userId, AccountDto accountDto);


    /**
     * Обновление существующего счёта.
     */
    AccountDto updateAccount(Long userId, Long accountId, AccountDto accountDto);


    /**
     * Перевод счёта в архив (деактивация).
     */
    void archiveAccount(Long userId, Long accountId);


    /**
     * Удаление счёта (при отсутствии связанных транзакций либо
     * с учётом бизнес-ограничений).
     */
    void deleteAccount(Long userId, Long accountId);


    /**
     * Получение счёта по идентификатору (с проверкой принадлежности пользователю).
     */
    Optional<AccountDto> getAccountById(Long userId, Long accountId);


    /**
     * Получение всех активных счетов пользователя.
     */
    List<AccountDto> getActiveAccounts(Long userId);


    /**
     * Получение всех счетов пользователя (включая архивные).
     */
    List<AccountDto> getAllAccounts(Long userId);
}