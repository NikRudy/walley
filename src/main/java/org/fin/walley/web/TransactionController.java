package org.fin.walley.web;
import jakarta.validation.Valid;
import org.fin.walley.domain.Transaction;
import org.fin.walley.domain.TransactionType;
import org.fin.walley.service.CategoryService;
import org.fin.walley.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.LocalDate;


@Controller
@RequestMapping("/transactions")
public class TransactionController {


    private final TransactionService txService;
    private final CategoryService categoryService;


    public TransactionController(TransactionService txService, CategoryService categoryService) {
        this.txService = txService;
        this.categoryService = categoryService;
    }


    @GetMapping
    public String list(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("tx", txService.listForUser(username));
        return "transactions";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        Transaction tx = new Transaction();
        tx.setType(TransactionType.EXPENSE);
        tx.setDate(LocalDate.now());
        model.addAttribute("tx", tx);
        model.addAttribute("types", TransactionType.values());
        return "transaction-form";
    }


    @PostMapping
    public String create(@ModelAttribute("tx") @Valid Transaction tx,
                         BindingResult binding,
                         Principal principal,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("types", TransactionType.values());
            return "transaction-form";
        }
        txService.create(principal.getName(), tx);
        return "redirect:/transactions";
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        Transaction tx = txService.findOwned(principal.getName(), id);
        model.addAttribute("tx", tx);
        model.addAttribute("types", TransactionType.values());
        return "transaction-form";
    }


    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("tx") @Valid Transaction tx,
                         BindingResult binding,
                         Principal principal,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("types", TransactionType.values());
            return "transaction-form";
        }
        txService.update(principal.getName(), id, tx);
        return "redirect:/transactions";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        txService.delete(principal.getName(), id);
        return "redirect:/transactions";
    }
}