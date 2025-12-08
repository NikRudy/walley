package org.fin.walley.repository.finance;


import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Репозиторий для работы с категориями финансовых операций.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Все активные категории (active = true).
     * Используется при построении справочников и форм.
     */
    List<Category> findByActiveTrue();


    /**
     * Активные категории, отфильтрованные по типу операции
     * (доход/расход). Например, все категории расходов.
     */
    List<Category> findByTypeAndActiveTrue(TransactionType type);
}