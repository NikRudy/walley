package org.fin.walley.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.Subcategory;
import org.fin.walley.domain.Transaction;
import org.fin.walley.domain.TransactionType;
import org.fin.walley.repo.CategoryRepository;
import org.fin.walley.repo.SubcategoryRepository;
import org.fin.walley.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService txService;
    private final CategoryRepository catRepo;
    private final SubcategoryRepository subRepo;

    public TransactionController(TransactionService txService,
                                 CategoryRepository catRepo,
                                 SubcategoryRepository subRepo) {
        this.txService = txService;
        this.catRepo = catRepo;
        this.subRepo = subRepo;
    }


    public static class TransactionForm {
        private Long id;

        @NotNull
        private TransactionType type;

        @NotNull
        @DecimalMin(value = "0.00", inclusive = false)
        private BigDecimal amount;

        @NotNull
        private LocalDate date;

        @NotNull
        private Long categoryId;

        private Long subcategoryId;

        @Size(max = 255)
        private String note;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public TransactionType getType() { return type; }
        public void setType(TransactionType type) { this.type = type; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public Long getSubcategoryId() { return subcategoryId; }
        public void setSubcategoryId(Long subcategoryId) { this.subcategoryId = subcategoryId; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        public Transaction toEntity() {
            Transaction tx = new Transaction();
            tx.setId(this.id);
            tx.setType(this.type);
            tx.setAmount(this.amount);
            tx.setDate(this.date);
            tx.setNote(this.note);
            return tx;
        }

        public static TransactionForm fromEntity(Transaction tx) {
            TransactionForm f = new TransactionForm();
            f.setId(tx.getId());
            f.setType(tx.getType());
            f.setAmount(tx.getAmount());
            f.setDate(tx.getDate());
            f.setNote(tx.getNote());
            if (tx.getCategory() != null) f.setCategoryId(tx.getCategory().getId());
            if (tx.getSubcategory() != null) f.setSubcategoryId(tx.getSubcategory().getId());
            return f;
        }
    }


    @GetMapping
    public String list(Principal principal, Authentication auth, Model model) {
        String username = principal.getName();

        List<Transaction> tx = txService.listForUser(username);

        LocalDate today = LocalDate.now(ZoneId.of("Europe/Warsaw"));
        TransactionService.Totals totals = txService.totalsForUserUpTo(username, today);

        model.addAttribute("tx", tx);
        model.addAttribute("asOf", today);
        model.addAttribute("incomeTotal", totals.income());
        model.addAttribute("expenseTotal", totals.expense());
        model.addAttribute("balance", totals.balance());
        model.addAttribute("isAdmin", isAdmin(auth));

        return "transactions";
    }


    @GetMapping("/new")
    public String createForm(Principal principal, Authentication auth, Model model) {
        TransactionForm form = new TransactionForm();
        form.setType(TransactionType.EXPENSE);
        form.setDate(LocalDate.now(ZoneId.of("Europe/Warsaw")));


        List<Category> categories = loadCategories(principal.getName(), form.getType());
        boolean noCategories = categories.isEmpty();

        if (!noCategories) {

            form.setCategoryId(categories.get(0).getId());
        }

        List<Subcategory> subs = (!noCategories && form.getCategoryId() != null)
                ? loadSubcategories(principal.getName(), form.getCategoryId())
                : Collections.emptyList();

        model.addAttribute("form", form);
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("categories", categories);
        model.addAttribute("subcategories", subs);
        model.addAttribute("noCategories", noCategories);
        model.addAttribute("isAdmin", isAdmin(auth));

        return "transaction-form";
    }

    @PostMapping
    public String create(Principal principal,
                         Authentication auth,
                         @ModelAttribute("form") @Valid TransactionForm form,
                         BindingResult binding,
                         Model model) {

        String username = principal.getName();


        List<Category> categories = loadCategories(username, form.getType());
        boolean noCategories = categories.isEmpty();

        if (noCategories) {
            binding.reject("noCategories", "No categories found for selected type.");
        }

        if (binding.hasErrors()) {
            List<Subcategory> subs = (form.getCategoryId() != null)
                    ? loadSubcategories(username, form.getCategoryId())
                    : Collections.emptyList();

            model.addAttribute("types", TransactionType.values());
            model.addAttribute("categories", categories);
            model.addAttribute("subcategories", subs);
            model.addAttribute("noCategories", noCategories);
            model.addAttribute("isAdmin", isAdmin(auth));
            return "transaction-form";
        }

        try {
            txService.create(username, form.toEntity(), form.getCategoryId(), form.getSubcategoryId());
            return "redirect:/transactions";
        } catch (IllegalArgumentException ex) {
            binding.reject("business", ex.getMessage());

            List<Subcategory> subs = (form.getCategoryId() != null)
                    ? loadSubcategories(username, form.getCategoryId())
                    : Collections.emptyList();

            model.addAttribute("types", TransactionType.values());
            model.addAttribute("categories", categories);
            model.addAttribute("subcategories", subs);
            model.addAttribute("noCategories", noCategories);
            model.addAttribute("isAdmin", isAdmin(auth));
            return "transaction-form";
        }
    }



    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Principal principal, Authentication auth, Model model) {
        String username = principal.getName();

        Transaction tx = txService.findOwned(username, id);
        TransactionForm form = TransactionForm.fromEntity(tx);

        List<Category> categories = loadCategories(username, form.getType());
        boolean noCategories = categories.isEmpty();


        if (!noCategories && form.getCategoryId() == null) {
            form.setCategoryId(categories.get(0).getId());
        }

        List<Subcategory> subs = (form.getCategoryId() != null)
                ? loadSubcategories(username, form.getCategoryId())
                : Collections.emptyList();

        model.addAttribute("form", form);
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("categories", categories);
        model.addAttribute("subcategories", subs);
        model.addAttribute("noCategories", noCategories);
        model.addAttribute("isAdmin", isAdmin(auth));

        return "transaction-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         Principal principal,
                         Authentication auth,
                         @ModelAttribute("form") @Valid TransactionForm form,
                         BindingResult binding,
                         Model model) {

        String username = principal.getName();
        form.setId(id);

        List<Category> categories = loadCategories(username, form.getType());
        boolean noCategories = categories.isEmpty();

        if (noCategories) {
            binding.reject("noCategories", "No categories found for selected type.");
        }

        if (binding.hasErrors()) {
            List<Subcategory> subs = (form.getCategoryId() != null)
                    ? loadSubcategories(username, form.getCategoryId())
                    : Collections.emptyList();

            model.addAttribute("types", TransactionType.values());
            model.addAttribute("categories", categories);
            model.addAttribute("subcategories", subs);
            model.addAttribute("noCategories", noCategories);
            model.addAttribute("isAdmin", isAdmin(auth));
            return "transaction-form";
        }

        try {
            txService.update(username, id, form.toEntity(), form.getCategoryId(), form.getSubcategoryId());
            return "redirect:/transactions";
        } catch (IllegalArgumentException ex) {
            binding.reject("business", ex.getMessage());

            List<Subcategory> subs = (form.getCategoryId() != null)
                    ? loadSubcategories(username, form.getCategoryId())
                    : Collections.emptyList();

            model.addAttribute("types", TransactionType.values());
            model.addAttribute("categories", categories);
            model.addAttribute("subcategories", subs);
            model.addAttribute("noCategories", noCategories);
            model.addAttribute("isAdmin", isAdmin(auth));
            return "transaction-form";
        }
    }



    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        txService.delete(principal.getName(), id);
        return "redirect:/transactions";
    }


    private boolean isAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }


    private List<Category> loadCategories(String username, TransactionType type) {

        return catRepo.findByUserUsernameAndTypeOrderByNameAsc(username, type);
    }

    private List<Subcategory> loadSubcategories(String username, Long categoryId) {

        return subRepo.findByCategoryIdAndCategoryUserUsernameOrderByNameAsc(categoryId, username);
    }
}
