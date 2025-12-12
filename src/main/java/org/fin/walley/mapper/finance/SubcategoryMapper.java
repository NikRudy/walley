package org.fin.walley.mapper.finance;

import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.Subcategory;
import org.fin.walley.dto.finance.SubcategoryDto;

/**
 * Маппер между Subcategory и SubcategoryDto.
 */
public final class SubcategoryMapper {

    private SubcategoryMapper() {
    }

    public static SubcategoryDto toDto(Subcategory subcategory) {
        if (subcategory == null) {
            return null;
        }

        Category category = subcategory.getCategory();

        return SubcategoryDto.builder()
                .id(subcategory.getId())
                .name(subcategory.getName())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .active(subcategory.isActive())
                .build();
    }

    public static void updateSubcategoryFromDto(SubcategoryDto dto, Subcategory subcategory, Category category) {
        if (dto == null || subcategory == null) {
            return;
        }
        subcategory.setName(dto.getName());
        // категория может меняться – новая передаётся параметром
        if (category != null) {
            subcategory.setCategory(category);
        }
        // active меняем в бизнес-операциях activate/deactivate
    }
}
