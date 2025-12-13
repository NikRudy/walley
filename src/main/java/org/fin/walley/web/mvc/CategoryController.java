package org.fin.walley.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.finance.TransactionType;
import org.fin.walley.dto.finance.CategoryDto;
import org.fin.walley.dto.finance.SubcategoryDto;
import org.fin.walley.service.finance.CategoryService;
import org.fin.walley.service.finance.SubcategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SubcategoryService subcategoryService;

    // --------- Список категорий и подкатегорий ---------

    @GetMapping
    public String listCategories(Model model) {
        List<CategoryDto> allCategories = categoryService.getAllCategories(false);
        List<SubcategoryDto> allSubcategories = subcategoryService.getAllSubcategories(false);

        model.addAttribute("categories", allCategories);
        model.addAttribute("subcategories", allSubcategories);
        model.addAttribute("transactionTypes", TransactionType.values());

        return "admin/categories"; // templates/admin/categories.html
    }

    // --------- Создание категории ---------

    @GetMapping("/new")
    public String showCreateCategoryForm(Model model) {
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new CategoryDto());
        }
        model.addAttribute("transactionTypes", TransactionType.values());
        return "admin/category-form"; // templates/admin/category-form.html
    }

    @PostMapping
    public String createCategory(@Valid @ModelAttribute("category") CategoryDto form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("transactionTypes", TransactionType.values());
            return "admin/category-form";
        }

        categoryService.createCategory(form);
        redirectAttributes.addFlashAttribute("successMessage", "Категория создана.");
        return "redirect:/admin/categories";
    }

    // --------- Редактирование категории ---------

    @GetMapping("/{id}/edit")
    public String showEditCategoryForm(@PathVariable("id") Long id,
                                       Model model) {
        if (!model.containsAttribute("category")) {
            CategoryDto dto = categoryService.getCategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
            model.addAttribute("category", dto);
        }
        model.addAttribute("transactionTypes", TransactionType.values());
        return "admin/category-form";
    }

    @PostMapping("/{id}")
    public String updateCategory(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("category") CategoryDto form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("transactionTypes", TransactionType.values());
            return "admin/category-form";
        }

        categoryService.updateCategory(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Категория обновлена.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/activate")
    public String activateCategory(@PathVariable("id") Long id,
                                   RedirectAttributes redirectAttributes) {
        categoryService.activateCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Категория активирована.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateCategory(@PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        categoryService.deactivateCategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Категория деактивирована.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Категория удалена.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // --------- Создание/редактирование подкатегории ---------

    @GetMapping("/subcategories/new")
    public String showCreateSubcategoryForm(@RequestParam("categoryId") Long categoryId,
                                            Model model) {
        if (!model.containsAttribute("subcategory")) {
            SubcategoryDto dto = SubcategoryDto.builder()
                    .categoryId(categoryId)
                    .build();
            model.addAttribute("subcategory", dto);
        }
        model.addAttribute("categories", categoryService.getAllCategories(true));
        return "admin/subcategory-form"; // templates/admin/subcategory-form.html
    }

    @PostMapping("/subcategories")
    public String createSubcategory(@Valid @ModelAttribute("subcategory") SubcategoryDto form,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories(true));
            return "admin/subcategory-form";
        }

        subcategoryService.createSubcategory(form);
        redirectAttributes.addFlashAttribute("successMessage", "Подкатегория создана.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/subcategories/{id}/edit")
    public String showEditSubcategoryForm(@PathVariable("id") Long id,
                                          Model model) {
        if (!model.containsAttribute("subcategory")) {
            SubcategoryDto dto = subcategoryService.getSubcategoryById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Subcategory not found: " + id));
            model.addAttribute("subcategory", dto);
        }
        model.addAttribute("categories", categoryService.getAllCategories(true));
        return "admin/subcategory-form";
    }

    @PostMapping("/subcategories/{id}")
    public String updateSubcategory(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("subcategory") SubcategoryDto form,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories(true));
            return "admin/subcategory-form";
        }

        subcategoryService.updateSubcategory(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Подкатегория обновлена.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/subcategories/{id}/activate")
    public String activateSubcategory(@PathVariable("id") Long id,
                                      RedirectAttributes redirectAttributes) {
        subcategoryService.activateSubcategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Подкатегория активирована.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/subcategories/{id}/deactivate")
    public String deactivateSubcategory(@PathVariable("id") Long id,
                                        RedirectAttributes redirectAttributes) {
        subcategoryService.deactivateSubcategory(id);
        redirectAttributes.addFlashAttribute("successMessage", "Подкатегория деактивирована.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/subcategories/{id}/delete")
    public String deleteSubcategory(@PathVariable("id") Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            subcategoryService.deleteSubcategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Подкатегория удалена.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
