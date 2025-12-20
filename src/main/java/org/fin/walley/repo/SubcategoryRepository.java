package org.fin.walley.repo;


import org.fin.walley.domain.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {


    List<Subcategory> findByCategoryIdAndCategoryUserUsernameOrderByNameAsc(Long categoryId, String username);


    Optional<Subcategory> findByIdAndCategoryUserUsername(Long id, String username);


    Optional<Subcategory> findByCategoryIdAndNameAndCategoryUserUsername(Long categoryId, String name, String username);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Subcategory s where s.category.user.id = :userId")
    void deleteAllForUser(@Param("userId") Long userId);
}