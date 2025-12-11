package org.fin.walley.dto.finance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;


/**
 * DTO счёта пользователя.
 * <p>
 * Используется в формах создания/редактирования и при
 * отображении списка счетов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {


    private Long id;


    private String name;


    private String description;


    /**
     * Текущий баланс счёта. Может заполняться на уровне сервиса,
     * на основе агрегированных данных по транзакциям.
     */
    private BigDecimal currentBalance;


    /**
     * Признак, что счет активен (не архивирован).
     */
    private boolean active;
}