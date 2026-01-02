package org.fin.walley.service;

import org.fin.walley.domain.Category;
import org.fin.walley.domain.Subcategory;
import org.fin.walley.repo.CategoryRepository;
import org.fin.walley.repo.SubcategoryRepository;
import org.fin.walley.repo.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubcategoryService {

    private final SubcategoryRepository subRepo;
    private final CategoryRepository catRepo;
    private final TransactionRepository txRepo;

    public SubcategoryService(SubcategoryRepository subRepo,
                              CategoryRepository catRepo,
                              TransactionRepository txRepo) {
        this.subRepo = subRepo;
        this.catRepo = catRepo;
        this.txRepo = txRepo;
    }

    @Transactional(readOnly = true)
    public List<Subcategory> listForCategory(String username, Long categoryId) {
        return subRepo.findByCategoryIdAndCategoryUserUsernameOrderByNameAsc(categoryId, username);
    }

    @Transactional(readOnly = true)
    public Subcategory findOwned(String username, Long id) {
        return subRepo.findByIdAndCategoryUserUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Subcategory not found"));
    }

    @Transactional(readOnly = true)
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

        findOwned(username, id);
        txRepo.clearSubcategory(username, id);
        subRepo.deleteById(id);
    }
}