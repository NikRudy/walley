package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий для работы с категориями финансовых операций.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
// Методы поиска активных категорий и по типу операции
// будут добавлены на следующем подэтапе.
}