package org.fin.walley.repo;


import org.fin.walley.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUsernameOrderByDateDescIdDesc(String username);
    Optional<Transaction> findByIdAndUserUsername(Long id, String username);
}