package org.fin.walley.repo;


import org.fin.walley.domain.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {


    List<Subcategory> findByCategoryIdAndCategoryUserUsernameOrderByNameAsc(Long categoryId, String username);


    Optional<Subcategory> findByIdAndCategoryUserUsername(Long id, String username);


    Optional<Subcategory> findByCategoryIdAndNameAndCategoryUserUsername(Long categoryId, String name, String username);
}