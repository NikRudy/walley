package org.fin.walley.web;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Role;
import org.fin.walley.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;


@Controller
@RequestMapping("/admin/users")
public class AdminUserController {


    private final UserService userService;


    public AdminUserController(UserService userService) {
        this.userService = userService;
    }


    public static class UserForm {
        @NotBlank
        private String username;


        @Size(min = 0, max = 72)
        private String password; // optional on edit


        private Role role = Role.USER;
        private boolean enabled = true;


        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }


    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin-users";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new UserForm());
        model.addAttribute("roles", Role.values());
        return "admin-user-form";
    }


    @PostMapping
    public String create(@ModelAttribute("form") @Valid UserForm form,
                         BindingResult binding,
                         Model model) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            binding.rejectValue("password", "required", "Password is required");
        }
        if (!binding.hasErrors() && userService.usernameTaken(form.getUsername())) {
            binding.rejectValue("username", "taken", "Username is already taken");
        }
        if (binding.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin-user-form";
        }
        userService.createByAdmin(form.getUsername(), form.getPassword(), form.getRole(), form.isEnabled());
        return "redirect:/admin/users";
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AppUser u = userService.findById(id);
        UserForm form = new UserForm();
        form.setUsername(u.getUsername());
        form.setRole(u.getRole());
        form.setEnabled(u.isEnabled());
        model.addAttribute("userId", id);
        model.addAttribute("form", form);
        model.addAttribute("roles", Role.values());
        return "admin-user-form";
    }


    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") @Valid UserForm form,
                         BindingResult binding,
                         Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("roles", Role.values());
            return "admin-user-form";
        }
        userService.updateByAdmin(id, form.getPassword(), form.getRole(), form.isEnabled());
        return "redirect:/admin/users";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}