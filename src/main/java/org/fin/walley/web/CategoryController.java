package org.fin.walley.web;


import jakarta.validation.Valid;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.TransactionType;
import org.fin.walley.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;


@Controller
@RequestMapping("/categories")
public class CategoryController {


    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public String list(Model model, Principal principal) {
        model.addAttribute("categories", categoryService.listForUser(principal.getName()));
        return "categories";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        Category c = new Category();
        c.setType(TransactionType.EXPENSE);
        model.addAttribute("category", c);
        model.addAttribute("types", TransactionType.values());
        return "category-form";
    }


    @PostMapping
    public String create(@ModelAttribute("category") @Valid Category c,
                         BindingResult binding,
                         Principal principal,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("types", TransactionType.values());
            return "category-form";
        }
        categoryService.create(principal.getName(), c);
        return "redirect:/categories";
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("category", categoryService.findOwned(principal.getName(), id));
        model.addAttribute("types", TransactionType.values());
        return "category-form";
    }


    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("category") @Valid Category c,
                         BindingResult binding,
                         Principal principal,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("types", TransactionType.values());
            return "category-form";
        }
        categoryService.update(principal.getName(), id, c);
        return "redirect:/categories";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        categoryService.delete(principal.getName(), id);
        return "redirect:/categories";
    }
}