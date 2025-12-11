

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fin.walley.domain.finance.TransactionType;


import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * DTO финансовой транзакции.
 * <p>
 * Представляет данные, необходимые для отображения и
 * создания/редактирования операций в UI или через REST.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {


    private Long id;


    /**
     * Идентификатор пользователя-владельца транзакции.
     * Обычно не заполняется с UI напрямую, а подставляется
     * на уровне сервисного слоя из текущего контекста безопасности.
     */
    private Long userId;


    private Long accountId;


    private Long categoryId;


    private Long subcategoryId;


    private BigDecimal amount;


    private TransactionType type;


    /**
     * Момент совершения операции.
     */
    private LocalDateTime occurredAt;


    /**
     * Произвольный комментарий пользователя.
     */
    private String description;


    /**
     * Флаг логического удаления (для soft delete сценариев).
     */
    private boolean deleted;
}