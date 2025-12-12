package org.fin.walley.service.finance;

import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.TransactionType;
import org.fin.walley.dto.finance.CategoryDto;
import org.fin.walley.repository.finance.CategoryRepository;
import org.fin.walley.repository.finance.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Реализация сервиса категорий (глобальный справочник категорий).
 */
@Service
@RequiredArgsConstructor
@Transactional
class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setType(dto.getType());
        category.setActive(true);

        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto dto) {
        Category category = getCategoryOrThrow(categoryId);
        category.setName(dto.getName());
        category.setType(dto.getType());

        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    @Override
    public void deactivateCategory(Long categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    public void activateCategory(Long categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        category.setActive(true);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryOrThrow(categoryId);

        boolean hasTransactions = transactionRepository.findAll().stream()
                .anyMatch(t -> t.getCategory() != null && t.getCategory().getId().equals(categoryId));

        if (hasTransactions) {
            throw new IllegalStateException("Cannot delete category with existing transactions");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByType(TransactionType type, boolean onlyActive) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getType() == type)
                .filter(c -> !onlyActive || c.isActive())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(boolean onlyActive) {
        return categoryRepository.findAll().stream()
                .filter(c -> !onlyActive || c.isActive())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .active(category.isActive())
                .build();
    }
}