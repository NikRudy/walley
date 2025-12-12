package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fin.walley.domain.finance.TransactionType;


import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * DTO для транзакции (доход/расход).
 * Здесь валидация гарантирует корректность формы (UI-уровень),
 * а принадлежность счёта/категории пользователю проверяется в сервисах.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {


    private Long id;


    /**
     * Владелец транзакции. Обычно определяется из контекста безопасности,
     * поэтому не помечаем как @NotNull, чтобы DTO было универсальным
     * (к примеру, в админских сценариях).
     */
    private Long userId;


    @NotNull(message = "{transaction.accountId.not-null}")
    private Long accountId;


    @NotNull(message = "{transaction.categoryId.not-null}")
    private Long categoryId;


    /**
     * Подкатегория может быть необязательной, поэтому без @NotNull.
     */
    private Long subcategoryId;


    @NotNull(message = "{transaction.amount.not-null}")
    @Positive(message = "{transaction.amount.positive}")
    private BigDecimal amount;


    @NotNull(message = "{transaction.type.not-null}")
    private TransactionType type;


    @NotNull(message = "{transaction.occurredAt.not-null}")
    private LocalDateTime occurredAt;


    @Size(max = 500, message = "{transaction.description.size}")
    private String description;


    /**
     * Флаг логического удаления – управляется бизнес-логикой,
     * поэтому валидацией не ограничивается.
     */
    private boolean deleted;
}