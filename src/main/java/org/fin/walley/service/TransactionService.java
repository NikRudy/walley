package org.fin.walley.service;


import org.fin.walley.domain.*;
import org.fin.walley.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class TransactionService {


    private final TransactionRepository txRepo;
    private final AppUserRepository users;
    private final CategoryRepository catRepo;
    private final SubcategoryRepository subRepo;


    public TransactionService(TransactionRepository txRepo,
                              AppUserRepository users,
                              CategoryRepository catRepo,
                              SubcategoryRepository subRepo) {
        this.txRepo = txRepo;
        this.users = users;
        this.catRepo = catRepo;
        this.subRepo = subRepo;
    }


    public List<Transaction> listForUser(String username) {
        return txRepo.findByUserUsernameOrderByDateDescIdDesc(username);
    }


    public Transaction findOwned(String username, Long id) {
        return txRepo.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }


    @Transactional
    public Transaction create(String username, Transaction tx, Long categoryId, Long subcategoryIdOrNull) {
        AppUser u = users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        Category cat = catRepo.findByIdAndUserUsername(categoryId, username)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));


// бизнес-правило: категория должна совпадать по типу (INCOME/EXPENSE)
        if (cat.getType() != tx.getType()) {
            throw new IllegalArgumentException("Category type must match transaction type");
        }


        Subcategory sub = null;
        if (subcategoryIdOrNull != null) {
            sub = subRepo.findByIdAndCategoryUserUsername(subcategoryIdOrNull, username)
                    .orElseThrow(() -> new IllegalArgumentException("Subcategory not found"));
            if (!sub.getCategory().getId().equals(cat.getId())) {
                throw new IllegalArgumentException("Subcategory must belong to selected category");
            }
        }


        tx.setId(null);
        tx.setUser(u);
        tx.setCategory(cat);
        tx.setSubcategory(sub);
        return txRepo.save(tx);
    }


    @Transactional
    public Transaction update(String username, Long id, Transaction form, Long categoryId, Long subcategoryIdOrNull) {
        Transaction tx = findOwned(username, id);


        Category cat = catRepo.findByIdAndUserUsername(categoryId, username)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        if (cat.getType() != form.getType()) {
            throw new IllegalArgumentException("Category type must match transaction type");
        }


        Subcategory sub = null;
        if (subcategoryIdOrNull != null) {
            sub = subRepo.findByIdAndCategoryUserUsername(subcategoryIdOrNull, username)
                    .orElseThrow(() -> new IllegalArgumentException("Subcategory not found"));
            if (!sub.getCategory().getId().equals(cat.getId())) {
                throw new IllegalArgumentException("Subcategory must belong to selected category");
            }
        }


        tx.setType(form.getType());
        tx.setAmount(form.getAmount());
        tx.setDate(form.getDate());
        tx.setCategory(cat);
        tx.setSubcategory(sub);
        tx.setNote(form.getNote());
        return tx;
    }


    @Transactional
    public void delete(String username, Long id) {
        Transaction tx = findOwned(username, id);
        txRepo.delete(tx);
    }

    public AppUser requireUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}