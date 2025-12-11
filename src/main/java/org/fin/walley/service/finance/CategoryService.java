package org.fin.walley.service.finance;


import org.fin.walley.dto.finance.CategoryDto;
import org.fin.walley.domain.finance.TransactionType;


import java.util.List;
import java.util.Optional;


/**
 * Сервис управления категориями финансовых операций.
 */
public interface CategoryService {


    CategoryDto createCategory(Long userId, CategoryDto categoryDto);


    CategoryDto updateCategory(Long userId, Long categoryId, CategoryDto categoryDto);


    /**
     * Деактивация категории (перевод в неактивное состояние).
     */
    void deactivateCategory(Long userId, Long categoryId);


    /**
     * Активировать ранее деактивированную категорию.
     */
    void activateCategory(Long userId, Long categoryId);


    /**
     * Удаление категории с учётом наличия связанных транзакций.
     */
    void deleteCategory(Long userId, Long categoryId);


    Optional<CategoryDto> getCategoryById(Long userId, Long categoryId);


    /**
     * Список категорий для пользователя по типу операции (INCOME/EXPENSE).
     */
    List<CategoryDto> getCategoriesByType(Long userId, TransactionType type, boolean onlyActive);


    /**
     * Все категории пользователя.
     */
    List<CategoryDto> getAllCategories(Long userId);
}