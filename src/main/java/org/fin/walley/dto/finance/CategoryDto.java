package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.fin.walley.domain.finance.TransactionType;


/**
 * DTO для категорий (верхний уровень классификации операций).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {


    private Long id;


    @NotBlank(message = "{category.name.not-blank}")
    @Size(max = 100, message = "{category.name.size}")
    private String name;


    @NotNull(message = "{category.type.not-null}")
    private TransactionType type;


    private boolean active;
}