package org.fin.walley.web.mvc;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fin.walley.dto.user.UserDto;
import org.fin.walley.dto.user.UserRegistrationDto;
import org.fin.walley.service.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;


// ---------------- Регистрация ----------------


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UserRegistrationDto());
        }
        return "auth/register"; // шаблон src/main/resources/templates/auth/register.html
    }


    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute("form") UserRegistrationDto form,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/register";
        }


        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно. Теперь вы можете войти в систему.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("registration.error", ex.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/register";
        }
    }


// ---------------- Логин ----------------


    @GetMapping("/login")
    public String showLoginForm() {
// Шаблон логина обрабатывается Spring Security (formLogin),
// здесь только отдаём страницу.
        return "auth/login"; // templates/auth/login.html
    }


// ---------------- Профиль пользователя ----------------


    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails principal,
                              Model model) {
        UserDto user = userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.getUsername()));


        if (!model.containsAttribute("profile")) {
            model.addAttribute("profile", user);
        }
        return "user/profile"; // templates/user/profile.html
    }


    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @Valid @ModelAttribute("profile") UserDto profile,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        UserDto current = userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.getUsername()));


        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profile", bindingResult);
            redirectAttributes.addFlashAttribute("profile", profile);
            return "redirect:/profile";
        }


        try {
            userService.updateProfile(current.getId(), profile);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль обновлён.");
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("profile.update.error", ex.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profile", bindingResult);
            redirectAttributes.addFlashAttribute("profile", profile);
        }
        return "redirect:/profile";
    }
}