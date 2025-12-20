package org.fin.walley.service;


import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.TransactionType;
import org.fin.walley.repo.AppUserRepository;
import org.fin.walley.repo.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class CategoryService {


    private final CategoryRepository categories;
    private final AppUserRepository users;


    public CategoryService(CategoryRepository categories, AppUserRepository users) {
        this.categories = categories;
        this.users = users;
    }


    public List<Category> listForUser(String username) {
        return categories.findByUserUsernameOrderByNameAsc(username);
    }


    public List<Category> listForUserByType(String username, TransactionType type) {
        return categories.findByUserUsernameAndTypeOrderByNameAsc(username, type);
    }


    public Category findOwned(String username, Long id) {
        return categories.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }


    @Transactional
    public Category create(String username, Category c) {
        AppUser u = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        c.setId(null);
        c.setUser(u);
        return categories.save(c);
    }


    @Transactional
    public Category update(String username, Long id, Category form) {
        Category c = findOwned(username, id);
        c.setName(form.getName());
        c.setType(form.getType());
        return c;
    }


    @Transactional
    public void delete(String username, Long id) {
        Category c = findOwned(username, id);
        categories.delete(c);
    }
}