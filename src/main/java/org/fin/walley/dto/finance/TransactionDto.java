package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fin.walley.domain.finance.TransactionType;


import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * DTO финансовой транзакции.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {


    private Long id;


    /**
     * userId, как правило, не приходит с UI, а подставляется
     * сервисом из контекста безопасности, поэтому здесь не
     * валидируется.
     */
    private Long userId;


    @NotNull(message = "{validation.transaction.accountId.notNull}")
    private Long accountId;


    @NotNull(message = "{validation.transaction.categoryId.notNull}")
    private Long categoryId;


    // subcategoryId может быть null, если транзакция привязана только к категории.
    private Long subcategoryId;


    @NotNull(message = "{validation.transaction.amount.notNull}")
    @Positive(message = "{validation.transaction.amount.positive}")
    private BigDecimal amount;


    @NotNull(message = "{validation.transaction.type.notNull}")
    private TransactionType type;


    @NotNull(message = "{validation.transaction.occurredAt.notNull}")
    private LocalDateTime occurredAt;


    @Size(max = 500, message = "{validation.transaction.description.size}")
    private String description;


    private boolean deleted;
}