package org.fin.walley.mapper.finance;

import org.fin.walley.domain.finance.Category;
import org.fin.walley.dto.finance.CategoryDto;

/**
 * Маппер между Category и CategoryDto.
 */
public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .active(category.isActive())
                .build();
    }

    public static void updateCategoryFromDto(CategoryDto dto, Category category) {
        if (dto == null || category == null) {
            return;
        }
        category.setName(dto.getName());
        category.setType(dto.getType());
        // active меняется через методы activate/deactivate
    }
}
