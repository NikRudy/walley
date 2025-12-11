ackage org.fin.walley.dto.finance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fin.walley.domain.finance.TransactionType;


/**
 * DTO категории финансовых операций.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {


    private Long id;


    private String name;


    /**
     * Тип операции, к которой относится категория (доход/расход).
     */
    private TransactionType type;


    /**
     * Признак, что категория доступна для выбора новых транзакций.
     */
    private boolean active;
}