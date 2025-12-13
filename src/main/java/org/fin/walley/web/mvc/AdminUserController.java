package org.fin.walley.web.mvc;

import lombok.RequiredArgsConstructor;
import org.fin.walley.dto.user.UserDto;
import org.fin.walley.service.user.RoleService;
import org.fin.walley.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;

    // --------- Список пользователей ---------

    @GetMapping
    public String listUsers(Model model) {
        List<UserDto> users = userService.findAll();
        Set<String> roles = roleService.getAllRoleNames();

        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        return "admin/users"; // templates/admin/users.html
    }

    // --------- Блокировка / разблокировка ---------

    @PostMapping("/{id}/block")
    public String blockUser(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes) {
        userService.disableUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь заблокирован.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unblock")
    public String unblockUser(@PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {
        userService.enableUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь разблокирован.");
        return "redirect:/admin/users";
    }

    // --------- Заготовка под назначение ролей / удаление ---------
    // Чтобы не выходить за рамки уже реализованного сервисного слоя,
    // здесь можно добавить методы назначения ролей и удаления пользователя
    // после расширения UserService.
}
