package org.fin.walley.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.Transaction;
import org.fin.walley.domain.TransactionType;
import org.fin.walley.service.CategoryService;
import org.fin.walley.service.SubcategoryService;
import org.fin.walley.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService txService;
    private final CategoryService categoryService;
    private final SubcategoryService subcategoryService;

    public TransactionController(TransactionService txService,
                                 CategoryService categoryService,
                                 SubcategoryService subcategoryService) {
        this.txService = txService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
    }

    /**
     * DTO формы для Thymeleaf (не JPA-entity).
     * Храним categoryId/subcategoryId, а в сервис передаем отдельно.
     */
    public static class TxForm {

        private Long id;

        @NotNull
        private TransactionType type;

        @NotNull
        @Positive
        @Digits(integer = 12, fraction = 2)
        private BigDecimal amount;

        @NotNull
        private LocalDate date;

        @NotNull(message = "Category is required")
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

        static TxForm from(Transaction tx) {
            TxForm f = new TxForm();
            f.setId(tx.getId());
            f.setType(tx.getType());
            f.setAmount(tx.getAmount());
            f.setDate(tx.getDate());
            f.setNote(tx.getNote());
            f.setCategoryId(tx.getCategory() != null ? tx.getCategory().getId() : null);
            f.setSubcategoryId(tx.getSubcategory() != null ? tx.getSubcategory().getId() : null);
            return f;
        }

        Transaction toEntity() {
            Transaction t = new Transaction();
            t.setType(type);
            t.setAmount(amount);
            t.setDate(date);
            t.setNote(note);
            return t;
        }
    }

    @GetMapping
    public String list(Model model, Principal principal) {
        model.addAttribute("tx", txService.listForUser(principal.getName()));
        return "transactions";
    }

    @GetMapping("/new")
    public String createForm(Model model, Principal principal) {
        String username = principal.getName();

        TxForm form = new TxForm();
        form.setType(TransactionType.EXPENSE);
        form.setDate(LocalDate.now());

        List<Category> categories = categoryService.listForUserByType(username, form.getType());
        if (!categories.isEmpty()) {
            form.setCategoryId(categories.getFirst().getId());
        }

        model.addAttribute("form", form);
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("categories", categories);
        model.addAttribute("subcategories",
                form.getCategoryId() == null ? List.of() : subcategoryService.listForCategory(username, form.getCategoryId())
        );
        model.addAttribute("noCategories", categories.isEmpty());
        return "transaction-form";
    }

    @PostMapping
    public String create(@ModelAttribute("form") @Valid TxForm form,
                         BindingResult binding,
                         Principal principal,
                         Model model) {

        String username = principal.getName();

        if (binding.hasErrors()) {
            refillDropdowns(model, username, form);
            return "transaction-form";
        }

        txService.create(username, form.toEntity(), form.getCategoryId(), form.getSubcategoryId());
        return "redirect:/transactions";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        String username = principal.getName();

        Transaction tx = txService.findOwned(username, id);
        TxForm form = TxForm.from(tx);

        model.addAttribute("form", form);
        model.addAttribute("types", TransactionType.values());

        List<Category> categories = categoryService.listForUserByType(username, form.getType());
        model.addAttribute("categories", categories);

        model.addAttribute("subcategories",
                form.getCategoryId() == null ? List.of() : subcategoryService.listForCategory(username, form.getCategoryId())
        );
        model.addAttribute("noCategories", categories.isEmpty());

        return "transaction-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") @Valid TxForm form,
                         BindingResult binding,
                         Principal principal,
                         Model model) {

        String username = principal.getName();

        if (binding.hasErrors()) {
            refillDropdowns(model, username, form);
            return "transaction-form";
        }

        txService.update(username, id, form.toEntity(), form.getCategoryId(), form.getSubcategoryId());
        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        txService.delete(principal.getName(), id);
        return "redirect:/transactions";
    }

    private void refillDropdowns(Model model, String username, TxForm form) {
        model.addAttribute("types", TransactionType.values());

        List<Category> categories = categoryService.listForUserByType(username, form.getType());
        model.addAttribute("categories", categories);

        model.addAttribute("subcategories",
                form.getCategoryId() == null ? List.of() : subcategoryService.listForCategory(username, form.getCategoryId())
        );

        model.addAttribute("noCategories", categories.isEmpty());
    }
}
