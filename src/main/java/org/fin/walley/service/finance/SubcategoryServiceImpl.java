package org.fin.walley.service.finance;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.Subcategory;
import org.fin.walley.dto.finance.SubcategoryDto;
import org.fin.walley.repository.finance.CategoryRepository;
import org.fin.walley.repository.finance.SubcategoryRepository;
import org.fin.walley.repository.finance.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
class SubcategoryServiceImpl implements SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public SubcategoryDto createSubcategory(SubcategoryDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryId()));

        Subcategory subcategory = new Subcategory();
        subcategory.setName(dto.getName());
        subcategory.setActive(true);
        subcategory.setCategory(category);

        Subcategory saved = subcategoryRepository.save(subcategory);
        return toDto(saved);
    }

    @Override
    public SubcategoryDto updateSubcategory(Long subcategoryId, SubcategoryDto dto) {
        Subcategory subcategory = getSubcategoryOrThrow(subcategoryId);

        if (!subcategory.getCategory().getId().equals(dto.getCategoryId())) {
            Category newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryId()));
            subcategory.setCategory(newCategory);
        }

        subcategory.setName(dto.getName());

        Subcategory saved = subcategoryRepository.save(subcategory);
        return toDto(saved);
    }

    @Override
    public void deactivateSubcategory(Long subcategoryId) {
        Subcategory subcategory = getSubcategoryOrThrow(subcategoryId);
        subcategory.setActive(false);
        subcategoryRepository.save(subcategory);
    }

    @Override
    public void activateSubcategory(Long subcategoryId) {
        Subcategory subcategory = getSubcategoryOrThrow(subcategoryId);
        subcategory.setActive(true);
        subcategoryRepository.save(subcategory);
    }

    @Override
    public void deleteSubcategory(Long subcategoryId) {
        Subcategory subcategory = getSubcategoryOrThrow(subcategoryId);

        boolean hasTransactions = transactionRepository.findAll().stream()
                .anyMatch(t -> t.getSubcategory() != null && t.getSubcategory().getId().equals(subcategoryId));

        if (hasTransactions) {
            throw new IllegalStateException("Cannot delete subcategory with existing transactions");
        }

        subcategoryRepository.delete(subcategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubcategoryDto> getSubcategoryById(Long subcategoryId) {
        return subcategoryRepository.findById(subcategoryId)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryDto> getSubcategoriesByCategory(Long categoryId, boolean onlyActive) {
        return subcategoryRepository.findAll().stream()
                .filter(s -> s.getCategory() != null && s.getCategory().getId().equals(categoryId))
                .filter(s -> !onlyActive || s.isActive())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoryDto> getAllSubcategories(boolean onlyActive) {
        return subcategoryRepository.findAll().stream()
                .filter(s -> !onlyActive || s.isActive())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Subcategory getSubcategoryOrThrow(Long subcategoryId) {
        return subcategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Subcategory not found: " + subcategoryId));
    }

    private SubcategoryDto toDto(Subcategory subcategory) {
        return SubcategoryDto.builder()
                .id(subcategory.getId())
                .name(subcategory.getName())
                .categoryId(subcategory.getCategory() != null ? subcategory.getCategory().getId() : null)
                .categoryName(subcategory.getCategory() != null ? subcategory.getCategory().getName() : null)
                .active(subcategory.isActive())
                .build();
    }
}