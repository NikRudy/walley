package org.fin.walley.api;


import org.fin.walley.domain.Subcategory;
import org.fin.walley.service.SubcategoryService;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {


    private final SubcategoryService subService;


    public CategoryApiController(SubcategoryService subService) {
        this.subService = subService;
    }


    public record SubDto(Long id, String name) {
        static SubDto from(Subcategory s) {
            return new SubDto(s.getId(), s.getName());
        }
    }


    @GetMapping("/{categoryId}/subcategories")
    public List<SubDto> listSub(@PathVariable Long categoryId, Principal principal) {
        return subService.listForCategory(principal.getName(), categoryId)
                .stream().map(SubDto::from).toList();
    }
}