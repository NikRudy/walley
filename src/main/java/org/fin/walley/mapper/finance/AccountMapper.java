package org.fin.walley.mapper.finance;

import org.fin.walley.domain.finance.Account;
import org.fin.walley.dto.finance.AccountDto;

/**
 * Маппер между Account и AccountDto.
 */
public final class AccountMapper {

    private AccountMapper() {
    }

    public static AccountDto toDto(Account account) {
        if (account == null) {
            return null;
        }

        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .description(account.getDescription())
                .currentBalance(account.getCurrentBalance())
                .active(account.isActive())
                .build();
    }

    /**
     * Обновляет поля счёта на основе AccountDto.
     * Владелец (user) и коллекция транзакций изменяются в сервисах.
     */
    public static void updateAccountFromDto(AccountDto dto, Account account) {
        if (dto == null || account == null) {
            return;
        }
        account.setName(dto.getName());
        account.setDescription(dto.getDescription());
        account.setCurrentBalance(dto.getCurrentBalance());
        // active управляется отдельными операциями (archive/activate)
    }
}
