package org.fin.walley.service.finance;

package org.fin.walley.service.finance;


import org.fin.walley.dto.finance.SubcategoryDto;


import java.util.List;
import java.util.Optional;


/**
 * Сервис управления подкатегориями.
 */
public interface SubcategoryService {


    SubcategoryDto createSubcategory(Long userId, SubcategoryDto subcategoryDto);


    SubcategoryDto updateSubcategory(Long userId, Long subcategoryId, SubcategoryDto subcategoryDto);


    void deactivateSubcategory(Long userId, Long subcategoryId);


    void activateSubcategory(Long userId, Long subcategoryId);


    void deleteSubcategory(Long userId, Long subcategoryId);


    Optional<SubcategoryDto> getSubcategoryById(Long userId, Long subcategoryId);


    /**
     * Список подкатегорий для указанной категории.
     */
    List<SubcategoryDto> getSubcategoriesByCategory(Long userId, Long categoryId, boolean onlyActive);


    /**
     * Все подкатегории пользователя.
     */
    List<SubcategoryDto> getAllSubcategories(Long userId);
}