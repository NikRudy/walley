package org.fin.walley.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.finance.TransactionType;
import org.fin.walley.dto.finance.AccountDto;
import org.fin.walley.dto.finance.CategoryDto;
import org.fin.walley.dto.finance.SubcategoryDto;
import org.fin.walley.dto.finance.TransactionDto;
import org.fin.walley.dto.user.UserDto;
import org.fin.walley.service.finance.AccountService;
import org.fin.walley.service.finance.CategoryService;
import org.fin.walley.service.finance.SubcategoryService;
import org.fin.walley.service.finance.TransactionService;
import org.fin.walley.service.user.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final SubcategoryService subcategoryService;
    private final UserService userService;

    // --------- Список и фильтрация транзакций ---------

    @GetMapping
    public String listTransactions(@AuthenticationPrincipal UserDetails principal,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                   @RequestParam(required = false) TransactionType type,
                                   @RequestParam(required = false) Long accountId,
                                   @RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) Long subcategoryId,
                                   @RequestParam(required = false, defaultValue = "false") boolean includeDeleted,
                                   Model model) {
        UserDto user = getCurrentUser(principal);

        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : null;

        List<TransactionDto> transactions = transactionService.getTransactions(
                user.getId(),
                fromDateTime,
                toDateTime,
                type,
                accountId,
                categoryId,
                subcategoryId,
                includeDeleted
        );

        List<AccountDto> accounts = accountService.getActiveAccounts(user.getId());
        List<CategoryDto> incomeCategories = categoryService.getCategoriesByType(TransactionType.INCOME, true);
        List<CategoryDto> expenseCategories = categoryService.getCategoriesByType(TransactionType.EXPENSE, true);
        List<SubcategoryDto> allSubcategories = subcategoryService.getAllSubcategories(true);

        model.addAttribute("transactions", transactions);
        model.addAttribute("accounts", accounts);
        model.addAttribute("incomeCategories", incomeCategories);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("subcategories", allSubcategories);

        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedAccountId", accountId);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedSubcategoryId", subcategoryId);
        model.addAttribute("includeDeleted", includeDeleted);

        return "transactions/list"; // templates/transactions/list.html
    }

    // --------- Создание транзакции ---------

    @GetMapping("/new")
    public String showCreateForm(@AuthenticationPrincipal UserDetails principal,
                                 Model model) {
        UserDto user = getCurrentUser(principal);

        if (!model.containsAttribute("transaction")) {
            TransactionDto dto = new TransactionDto();
            dto.setOccurredAt(LocalDateTime.now());
            model.addAttribute("transaction", dto);
        }

        populateReferenceData(user.getId(), model);
        return "transactions/form"; // templates/transactions/form.html
    }

    @PostMapping
    public String createTransaction(@AuthenticationPrincipal UserDetails principal,
                                    @Valid @ModelAttribute("transaction") TransactionDto form,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        UserDto user = getCurrentUser(principal);

        if (bindingResult.hasErrors()) {
            populateReferenceData(user.getId(), model);
            return "transactions/form";
        }

        try {
            transactionService.createTransaction(user.getId(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Транзакция создана.");
            return "redirect:/transactions";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("transaction.create.error", ex.getMessage());
            populateReferenceData(user.getId(), model);
            return "transactions/form";
        }
    }

    // --------- Редактирование транзакции ---------

    @GetMapping("/{id}/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable("id") Long id,
                               Model model) {
        UserDto user = getCurrentUser(principal);

        if (!model.containsAttribute("transaction")) {
            TransactionDto tx = transactionService.getTransactionById(user.getId(), id)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
            model.addAttribute("transaction", tx);
        }

        populateReferenceData(user.getId(), model);
        return "transactions/form";
    }

    @PostMapping("/{id}")
    public String updateTransaction(@AuthenticationPrincipal UserDetails principal,
                                    @PathVariable("id") Long id,
                                    @Valid @ModelAttribute("transaction") TransactionDto form,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        UserDto user = getCurrentUser(principal);

        if (bindingResult.hasErrors()) {
            populateReferenceData(user.getId(), model);
            return "transactions/form";
        }

        try {
            transactionService.updateTransaction(user.getId(), id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Транзакция обновлена.");
            return "redirect:/transactions";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("transaction.update.error", ex.getMessage());
            populateReferenceData(user.getId(), model);
            return "transactions/form";
        }
    }

    // --------- Удаление транзакции (логическое) ---------

    @PostMapping("/{id}/delete")
    public String deleteTransaction(@AuthenticationPrincipal UserDetails principal,
                                    @PathVariable("id") Long id,
                                    RedirectAttributes redirectAttributes) {
        UserDto user = getCurrentUser(principal);
        transactionService.softDeleteTransaction(user.getId(), id);
        redirectAttributes.addFlashAttribute("successMessage", "Транзакция удалена (логически).");
        return "redirect:/transactions";
    }

    // --------- Вспомогательные методы ---------

    private UserDto getCurrentUser(UserDetails principal) {
        return userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.getUsername()));
    }

    private void populateReferenceData(Long userId, Model model) {
        List<AccountDto> accounts = accountService.getActiveAccounts(userId);
        List<CategoryDto> incomeCategories = categoryService.getCategoriesByType(TransactionType.INCOME, true);
        List<CategoryDto> expenseCategories = categoryService.getCategoriesByType(TransactionType.EXPENSE, true);
        List<SubcategoryDto> subcategories = subcategoryService.getAllSubcategories(true);

        model.addAttribute("accounts", accounts);
        model.addAttribute("incomeCategories", incomeCategories);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("subcategories", subcategories);
    }
}
