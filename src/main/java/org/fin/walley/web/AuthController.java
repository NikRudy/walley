package org.fin.walley.web;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.fin.walley.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;


@Controller
public class AuthController {


    private final UserService userService;


    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    public static class RegisterForm {
        @NotBlank
        private String username;


        @NotBlank
        @Size(min = 6, max = 72)
        private String password;


        @NotBlank
        @Size(min = 6, max = 72)
        private String confirmPassword;


        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }


    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }


    @PostMapping("/register")
    public String register(@ModelAttribute("form") @Valid RegisterForm form,
                           BindingResult binding,
                           Model model) {


        if (!form.getPassword().equals(form.getConfirmPassword())) {
            binding.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }


        if (!binding.hasErrors() && userService.usernameTaken(form.getUsername())) {
            binding.rejectValue("username", "taken", "Username is already taken");
        }


        if (binding.hasErrors()) {
            return "register";
        }


        userService.registerUser(form.getUsername(), form.getPassword());
        model.addAttribute("registered", true);
        return "redirect:/login?registered";
    }
}