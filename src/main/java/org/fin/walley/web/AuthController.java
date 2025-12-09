package org.fin.walley.web;

import lombok.RequiredArgsConstructor;
import org.fin.walley.service.user.UserRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для аутентификации и регистрации.
 */
@Controller
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationService userRegistrationService;

    // Страница логина (GET /login)
    @GetMapping("/login")
    public String showLoginForm() {
        // Вернём шаблон templates/auth/login.html
        return "auth/login";
    }

    // Страница регистрации (GET /register)
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Простейшая форма регистрации (username, email, password)
        model.addAttribute("form", new UserRegistrationService.RegisterRequest("", "", ""));
        // Вернём шаблон templates/auth/register.html
        return "auth/register";
    }

    // Обработка формы регистрации (POST /register)
    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("form") UserRegistrationService.RegisterRequest form) {
        userRegistrationService.registerNewUser(form);
        // После успешной регистрации отправляем на логин со флагом
        return "redirect:/login?registered=true";
    }
}
