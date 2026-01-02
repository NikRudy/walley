package org.fin.walley.service;

import org.fin.walley.domain.*;
import org.fin.walley.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Transactional(readOnly = true)
    public List<Transaction> listForUser(String username) {
        return txRepo.findByUserUsernameOrderByDateDescIdDesc(username);
    }

    @Transactional(readOnly = true)
    public Transaction findOwned(String username, Long id) {
        return txRepo.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    @Transactional(readOnly = true)
    public AppUser requireUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public Transaction create(String username, Transaction tx, Long categoryId, Long subcategoryIdOrNull) {
        AppUser u = requireUser(username);

        Category cat = catRepo.findByIdAndUserUsername(categoryId, username)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (cat.getType() != tx.getType()) {
            throw new IllegalArgumentException("Category type must match transaction type");
        }

        Subcategory sub = resolveSubcategory(username, cat, subcategoryIdOrNull);

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

        Subcategory sub = resolveSubcategory(username, cat, subcategoryIdOrNull);

        tx.setType(form.getType());
        tx.setAmount(form.getAmount());
        tx.setDate(form.getDate());
        tx.setCategory(cat);
        tx.setSubcategory(sub);
        tx.setNote(form.getNote());

        return txRepo.save(tx);
    }

    @Transactional
    public void delete(String username, Long id) {
        Transaction tx = findOwned(username, id);
        txRepo.delete(tx);
    }

    public record Totals(BigDecimal income, BigDecimal expense, BigDecimal balance) {}

    @Transactional(readOnly = true)
    public Totals totalsForUser(String username) {
        BigDecimal income = txRepo.sumAmountByUserAndType(username, TransactionType.INCOME);
        BigDecimal expense = txRepo.sumAmountByUserAndType(username, TransactionType.EXPENSE);
        BigDecimal balance = income.subtract(expense);
        return new Totals(income, expense, balance);
    }

    @Transactional(readOnly = true)
    public Totals totalsForUserUpTo(String username, LocalDate asOf) {
        BigDecimal income = txRepo.sumAmountByUserAndTypeUpToDate(username, TransactionType.INCOME, asOf);
        BigDecimal expense = txRepo.sumAmountByUserAndTypeUpToDate(username, TransactionType.EXPENSE, asOf);
        BigDecimal balance = income.subtract(expense);
        return new Totals(income, expense, balance);
    }

    private Subcategory resolveSubcategory(String username, Category cat, Long subcategoryIdOrNull) {
        if (subcategoryIdOrNull == null) {
            return null;
        }

        Subcategory sub = subRepo.findByIdAndCategoryUserUsername(subcategoryIdOrNull, username)
                .orElseThrow(() -> new IllegalArgumentException("Subcategory not found"));

        if (!sub.getCategory().getId().equals(cat.getId())) {
            throw new IllegalArgumentException("Subcategory must belong to selected category");
        }

        return sub;
    }
}
