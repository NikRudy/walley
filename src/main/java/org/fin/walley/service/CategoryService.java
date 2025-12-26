package org.fin.walley.service;

import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.TransactionType;
import org.fin.walley.repo.AppUserRepository;
import org.fin.walley.repo.CategoryRepository;
import org.fin.walley.repo.SubcategoryRepository;
import org.fin.walley.repo.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categories;
    private final AppUserRepository users;
    private final SubcategoryRepository subRepo;
    private final TransactionRepository txRepo;

    public CategoryService(CategoryRepository categories,
                           AppUserRepository users,
                           SubcategoryRepository subRepo,
                           TransactionRepository txRepo) {
        this.categories = categories;
        this.users = users;
        this.subRepo = subRepo;
        this.txRepo = txRepo;
    }

    @Transactional(readOnly = true)
    public List<Category> listForUser(String username) {
        return categories.findByUserUsernameOrderByNameAsc(username);
    }

    @Transactional(readOnly = true)
    public List<Category> listForUserByType(String username, TransactionType type) {
        return categories.findByUserUsernameAndTypeOrderByNameAsc(username, type);
    }

    @Transactional(readOnly = true)
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

    /**
     * Вариант A:
     * - транзакции НЕ удаляем
     * - перед удалением категории отвязываем category/subcategory у транзакций (-> NULL)
     * - удаляем все subcategory категории
     * - удаляем category
     */
    @Transactional
    public void delete(String username, Long id) {
        // проверка владения (и что категория существует)
        findOwned(username, id);

        // 1) отвязать category+subcategory у транзакций пользователя
        txRepo.clearCategory(username, id);

        // 2) удалить все подкатегории этой категории
        subRepo.deleteAllForCategory(username, id);

        // 3) удалить категорию
        categories.deleteById(id);
    }
}
