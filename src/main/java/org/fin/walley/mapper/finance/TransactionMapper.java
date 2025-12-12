package org.fin.walley.mapper.finance;

import org.fin.walley.domain.finance.Account;
import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.Subcategory;
import org.fin.walley.domain.finance.Transaction;
import org.fin.walley.domain.user.User;
import org.fin.walley.dto.finance.TransactionDto;

/**
 * Маппер между Transaction и TransactionDto.
 *
 * Обрати внимание: привязки к User, Account, Category, Subcategory
 * устанавливаются в сервисе (через репозитории), а сюда передаются
 * уже найденные сущности.
 */
public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionDto toDto(Transaction tx) {
        if (tx == null) {
            return null;
        }

        return TransactionDto.builder()
                .id(tx.getId())
                .userId(tx.getUser() != null ? tx.getUser().getId() : null)
                .accountId(tx.getAccount() != null ? tx.getAccount().getId() : null)
                .categoryId(tx.getCategory() != null ? tx.getCategory().getId() : null)
                .subcategoryId(tx.getSubcategory() != null ? tx.getSubcategory().getId() : null)
                .amount(tx.getAmount())
                .type(tx.getType())
                .occurredAt(tx.getOccurredAt())
                .description(tx.getDescription())
                .deleted(tx.isDeleted())
                .build();
    }

    /**
     * Заполняет поля транзакции на основе DTO и уже найденных сущностей.
     * Используется как при создании, так и при обновлении.
     */
    public static void applyFromDto(TransactionDto dto,
                                    Transaction tx,
                                    User user,
                                    Account account,
                                    Category category,
                                    Subcategory subcategory) {
        if (dto == null || tx == null) {
            return;
        }

        if (user != null) {
            tx.setUser(user);
        }
        if (account != null) {
            tx.setAccount(account);
        }
        if (category != null) {
            tx.setCategory(category);
        }
        tx.setSubcategory(subcategory); // может быть null

        tx.setAmount(dto.getAmount());
        tx.setType(dto.getType());
        tx.setOccurredAt(dto.getOccurredAt());
        tx.setDescription(dto.getDescription());
        // deleted управляется бизнес-логикой (soft delete / hard delete)
    }
}
