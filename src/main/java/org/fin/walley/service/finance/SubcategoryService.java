package org.fin.walley.service.finance;


import org.fin.walley.dto.finance.SubcategoryDto;


import java.util.List;
import java.util.Optional;


/**
 * Сервис для управления подкатегориями.
 */
interface SubcategoryService {

    /**
     * Создание новой подкатегории.
     */
    SubcategoryDto createSubcategory(SubcategoryDto subcategoryDto);

    /**
     * Обновление подкатегории.
     */
    SubcategoryDto updateSubcategory(Long subcategoryId, SubcategoryDto subcategoryDto);

    /**
     * Деактивация подкатегории.
     */
    void deactivateSubcategory(Long subcategoryId);

    /**
     * Активация подкатегории.
     */
    void activateSubcategory(Long subcategoryId);

    /**
     * Удаление подкатегории с проверкой отсутствия связанных транзакций.
     */
    void deleteSubcategory(Long subcategoryId);

    /**
     * Получение подкатегории по идентификатору.
     */
    Optional<SubcategoryDto> getSubcategoryById(Long subcategoryId);

    /**
     * Получение подкатегорий по идентификатору категории. При onlyActive=true
     * возвращаются только активные подкатегории.
     */
    List<SubcategoryDto> getSubcategoriesByCategory(Long categoryId, boolean onlyActive);

    /**
     * Получение всех подкатегорий. При onlyActive=true – только активные.
     */
    List<SubcategoryDto> getAllSubcategories(boolean onlyActive);
}