package org.fin.walley.web;


import jakarta.validation.Valid;
import org.fin.walley.domain.Category;
import org.fin.walley.domain.Subcategory;
import org.fin.walley.service.SubcategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;


@Controller
@RequestMapping("/categories/{categoryId}/subcategories")
public class SubcategoryController {


    private final SubcategoryService subService;


    public SubcategoryController(SubcategoryService subService) {
        this.subService = subService;
    }


    @GetMapping
    public String list(@PathVariable Long categoryId, Model model, Principal principal) {
        String username = principal.getName();
        Category cat = subService.findOwnedCategory(username, categoryId);
        model.addAttribute("category", cat);
        model.addAttribute("subcategories", subService.listForCategory(username, categoryId));
        return "subcategories";
    }


    @GetMapping("/new")
    public String createForm(@PathVariable Long categoryId, Model model, Principal principal) {
        Category cat = subService.findOwnedCategory(principal.getName(), categoryId);
        model.addAttribute("category", cat);
        model.addAttribute("sub", new Subcategory());
        return "subcategory-form";
    }


    @PostMapping
    public String create(@PathVariable Long categoryId,
                         @ModelAttribute("sub") @Valid Subcategory sub,
                         BindingResult binding,
                         Model model,
                         Principal principal) {
        if (binding.hasErrors()) {
            model.addAttribute("category", subService.findOwnedCategory(principal.getName(), categoryId));
            return "subcategory-form";
        }
        subService.create(principal.getName(), categoryId, sub);
        return "redirect:/categories/" + categoryId + "/subcategories";
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long categoryId, @PathVariable Long id, Model model, Principal principal) {
        Category cat = subService.findOwnedCategory(principal.getName(), categoryId);
        Subcategory sub = subService.findOwned(principal.getName(), id);
        model.addAttribute("category", cat);
        model.addAttribute("sub", sub);
        return "subcategory-form";
    }


    @PostMapping("/{id}")
    public String update(@PathVariable Long categoryId,
                         @PathVariable Long id,
                         @ModelAttribute("sub") @Valid Subcategory sub,
                         BindingResult binding,
                         Model model,
                         Principal principal) {
        if (binding.hasErrors()) {
            model.addAttribute("category", subService.findOwnedCategory(principal.getName(), categoryId));
            return "subcategory-form";
        }
        subService.update(principal.getName(), id, sub);
        return "redirect:/categories/" + categoryId + "/subcategories";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long categoryId, @PathVariable Long id, Principal principal) {
        subService.delete(principal.getName(), id);
        return "redirect:/categories/" + categoryId + "/subcategories";
    }
}