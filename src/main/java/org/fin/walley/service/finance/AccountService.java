package org.fin.walley.service.finance;


import org.fin.walley.dto.finance.AccountDto;


import java.util.List;
import java.util.Optional;


/**
 * Сервис для управления счетами пользователя.
 */
interface AccountService {

    /**
     * Создание нового счёта для пользователя.
     */
    AccountDto createAccount(Long userId, AccountDto accountDto);

    /**
     * Обновление параметров счёта пользователя.
     */
    AccountDto updateAccount(Long userId, Long accountId, AccountDto accountDto);

    /**
     * Архивация счёта (перевод в неактивное состояние).
     */
    void archiveAccount(Long userId, Long accountId);

    /**
     * Удаление счёта пользователя с контролем наличия транзакций.
     */
    void deleteAccount(Long userId, Long accountId);

    /**
     * Получение счёта по идентификатору для конкретного пользователя.
     */
    Optional<AccountDto> getAccountById(Long userId, Long accountId);

    /**
     * Список активных счетов пользователя.
     */
    List<AccountDto> getActiveAccounts(Long userId);

    /**
     * Список всех счетов пользователя (активных и архивных).
     */
    List<AccountDto> getAllAccounts(Long userId);
}