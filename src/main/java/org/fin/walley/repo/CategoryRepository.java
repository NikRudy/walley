package org.fin.walley.repo;


import org.fin.walley.domain.Category;
import org.fin.walley.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserUsernameOrderByNameAsc(String username);
    List<Category> findByUserUsernameAndTypeOrderByNameAsc(String username, TransactionType type);
    Optional<Category> findByIdAndUserUsername(Long id, String username);
    Optional<Category> findByUserUsernameAndTypeAndName(String username, TransactionType type, String name);
}