package org.fin.walley.repo;

import org.fin.walley.domain.Transaction;
import org.fin.walley.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserUsernameOrderByDateDescIdDesc(String username);

    Optional<Transaction> findByIdAndUserUsername(Long id, String username);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user.username = :username and t.type = :type
           """)
    BigDecimal sumAmountByUserAndType(@Param("username") String username,
                                      @Param("type") TransactionType type);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user.username = :username
              and t.type = :type
              and t.date <= :asOf
           """)
    BigDecimal sumAmountByUserAndTypeUpToDate(@Param("username") String username,
                                              @Param("type") TransactionType type,
                                              @Param("asOf") LocalDate asOf);

    // --------- ВАЖНО ДЛЯ УДАЛЕНИЯ ПОЛЬЗОВАТЕЛЯ ---------

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("delete from Transaction t where t.user.id = :userId")
    int deleteAllForUser(@Param("userId") Long userId);

    // --------- ВАЖНО ДЛЯ УДАЛЕНИЯ КАТЕГОРИИ / ПОДКАТЕГОРИИ ---------

    /**
     * Снять ссылку на subcategory у транзакций (транзакции остаются).
     * Работает только если в Transaction.subcategory join-column nullable=true.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("""
           update Transaction t
              set t.subcategory = null
            where t.user.username = :username
              and t.subcategory.id = :subId
           """)
    int clearSubcategory(@Param("username") String username,
                         @Param("subId") Long subId);

    /**
     * ВАЖНО: category обычно обязательная (nullable=false), поэтому "обнулить" нельзя.
     * Поэтому этот метод делает "очистку" категории через удаление транзакций этой категории.
     * Это позволяет удалить Category без FK-ошибок.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("""
           delete from Transaction t
            where t.user.username = :username
              and t.category.id = :categoryId
           """)
    int clearCategory(@Param("username") String username,
                      @Param("categoryId") Long categoryId);

    // (Опционально, для читаемости — то же самое, но с нормальным названием)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("""
           delete from Transaction t
            where t.user.username = :username
              and t.category.id = :categoryId
           """)
    int deleteAllForCategory(@Param("username") String username,
                             @Param("categoryId") Long categoryId);
}
