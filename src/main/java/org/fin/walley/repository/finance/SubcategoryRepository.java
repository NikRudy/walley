package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий для подкатегорий финансовых операций.
 */
@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
// Методы поиска активных подкатегорий по категории
// будут добавлены позже.
}