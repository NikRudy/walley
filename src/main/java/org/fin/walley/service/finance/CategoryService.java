package org.fin.walley.service.finance;


import org.fin.walley.dto.finance.CategoryDto;
import org.fin.walley.domain.finance.TransactionType;


import java.util.List;
import java.util.Optional;


/**
 * Сервис управления категориями финансовых операций.
 */
interface CategoryService {

    /**
     * Создание новой категории.
     */
    CategoryDto createCategory(CategoryDto categoryDto);

    /**
     * Обновление категории.
     */
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    /**
     * Деактивация категории (active=false).
     */
    void deactivateCategory(Long categoryId);

    /**
     * Активация категории (active=true).
     */
    void activateCategory(Long categoryId);

    /**
     * Удаление категории с проверкой отсутствия связанных транзакций.
     */
    void deleteCategory(Long categoryId);

    /**
     * Получение категории по идентификатору.
     */
    Optional<CategoryDto> getCategoryById(Long categoryId);

    /**
     * Получение категорий по типу (доход/расход). При onlyActive=true
     * возвращаются только активные категории.
     */
    List<CategoryDto> getCategoriesByType(TransactionType type, boolean onlyActive);

    /**
     * Получение всех категорий. При onlyActive=true – только активные.
     */
    List<CategoryDto> getAllCategories(boolean onlyActive);
}