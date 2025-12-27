package org.fin.walley.api;

import org.fin.walley.domain.TransactionType;
import org.fin.walley.service.CategoryService;
import org.fin.walley.service.SubcategoryService;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api")
public class CategoryApiController {


    private final CategoryService categoryService;
    private final SubcategoryService subcategoryService;


    public CategoryApiController(CategoryService categoryService, SubcategoryService subcategoryService) {
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
    }


    public record CategoryDto(Long id, String name) {}
    public record SubDto(Long id, String name) {}


    @GetMapping("/categories")
    public List<CategoryDto> categories(@RequestParam TransactionType type, Principal principal) {
        return categoryService.listForUserByType(principal.getName(), type)
                .stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }


    @GetMapping("/categories/{categoryId}/subcategories")
    public List<SubDto> subcategories(@PathVariable Long categoryId, Principal principal) {
        return subcategoryService.listForCategory(principal.getName(), categoryId)
                .stream()
                .map(s -> new SubDto(s.getId(), s.getName()))
                .toList();
    }
}