package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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


    @NotBlank(message = "{validation.category.name.notBlank}")
    @Size(min = 2, max = 100, message = "{validation.category.name.size}")
    private String name;


    @NotNull(message = "{validation.category.type.notNull}")
    private TransactionType type;


    private boolean active;
}