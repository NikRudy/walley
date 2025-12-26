package org.fin.walley.repo;


import org.fin.walley.domain.Transaction;
import org.fin.walley.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUsernameOrderByDateDescIdDesc(String username);
    List<Transaction> findAllByUserUsernameOrderByDateDescIdDesc(String username);
    Optional<Transaction> findByIdAndUserUsername(Long id, String username);
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Transaction t where t.user.id = :userId")
    void deleteAllForUser(@Param("userId") Long userId);

    @Query("select coalesce(sum(t.amount), 0) from Transaction t where t.user.username = :username and t.type = :type")
    BigDecimal sumAmountByUserAndType(@Param("username") String username, @Param("type") TransactionType type);

}