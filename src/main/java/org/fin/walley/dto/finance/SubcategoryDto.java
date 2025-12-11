package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * DTO подкатегории финансовых операций.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoryDto {


    private Long id;


    @NotBlank(message = "{validation.subcategory.name.notBlank}")
    @Size(min = 2, max = 100, message = "{validation.subcategory.name.size}")
    private String name;


    @NotNull(message = "{validation.subcategory.categoryId.notNull}")
    private Long categoryId;


    /**
     * categoryName используется только для отображения, поэтому
     * как правило не валидируется на уровне DTO.
     */
    private String categoryName;


    private boolean active;
}