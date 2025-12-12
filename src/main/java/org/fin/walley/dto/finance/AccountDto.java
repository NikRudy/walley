package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {


    private Long id;


    @NotBlank(message = "{account.name.not-blank}")
    @Size(max = 100, message = "{account.name.size}")
    private String name;


    @Size(max = 255, message = "{account.description.size}")
    private String description;


    @NotNull(message = "{account.currentBalance.not-null}")
    @PositiveOrZero(message = "{account.currentBalance.positive-or-zero}")
    private BigDecimal currentBalance;


    private boolean active;
}