package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Репозиторий для подкатегорий финансовых операций.
 */
@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    /**
     * Активные подкатегории внутри указанной категории.
     */
    List<Subcategory> findByCategoryAndActiveTrue(Category category);


    /**
     * Поиск подкатегории по имени внутри категории (регистронезависимо).
     */
    Optional<Subcategory> findByCategoryAndNameIgnoreCase(Category category, String name);
}