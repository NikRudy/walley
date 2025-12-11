package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;


/**
 * DTO счёта пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {


    private Long id;


    @NotBlank(message = "{validation.account.name.notBlank}")
    @Size(min = 2, max = 100, message = "{validation.account.name.size}")
    private String name;


    @Size(max = 255, message = "{validation.account.description.size}")
    private String description;


    /**
     * Текущий баланс счёта. Ввод баланса через UI может быть
     * ограничен только неотрицательными значениями, но при этом
     * бизнес-логика может получать отрицательный баланс в результате
     * расчётов. Поэтому валидация на уровне DTO обычно применяется
     * только для начальных/ручных значений.
     */
    @PositiveOrZero(message = "{validation.account.currentBalance.positiveOrZero}")
    private BigDecimal currentBalance;


    private boolean active;
}