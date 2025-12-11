package org.fin.walley.dto.finance;


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


    private String name;


    /**
     * Идентификатор родительской категории.
     */
    private Long categoryId;


    /**
     * Имя родительской категории (для удобства отображения в UI).
     */
    private String categoryName;


    private boolean active;
}