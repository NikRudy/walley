package org.fin.walley.service;


import org.fin.walley.domain.Category;
import org.fin.walley.domain.Subcategory;
import org.fin.walley.repo.CategoryRepository;
import org.fin.walley.repo.SubcategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class SubcategoryService {


    private final SubcategoryRepository subRepo;
    private final CategoryRepository catRepo;


    public SubcategoryService(SubcategoryRepository subRepo, CategoryRepository catRepo) {
        this.subRepo = subRepo;
        this.catRepo = catRepo;
    }


    public List<Subcategory> listForCategory(String username, Long categoryId) {
        return subRepo.findByCategoryIdAndCategoryUserUsernameOrderByNameAsc(categoryId, username);
    }


    public Subcategory findOwned(String username, Long id) {
        return subRepo.findByIdAndCategoryUserUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Subcategory not found"));
    }


    public Category findOwnedCategory(String username, Long categoryId) {
        return catRepo.findByIdAndUserUsername(categoryId, username)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }


    @Transactional
    public Subcategory create(String username, Long categoryId, Subcategory form) {
        Category cat = findOwnedCategory(username, categoryId);
        form.setId(null);
        form.setCategory(cat);
        return subRepo.save(form);
    }


    @Transactional
    public Subcategory update(String username, Long id, Subcategory form) {
        Subcategory s = findOwned(username, id);
        s.setName(form.getName());
        return s;
    }


    @Transactional
    public void delete(String username, Long id) {
        Subcategory s = findOwned(username, id);
        subRepo.delete(s);
    }
}