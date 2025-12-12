package org.fin.walley.dto.finance;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoryDto {


    private Long id;


    @NotBlank(message = "{subcategory.name.not-blank}")
    @Size(max = 100, message = "{subcategory.name.size}")
    private String name;


    @NotNull(message = "{subcategory.categoryId.not-null}")
    private Long categoryId;


    /**
     * Имя родительской категории – удобно для отображения в UI.
     */
    private String categoryName;


    private boolean active;
}