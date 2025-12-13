package org.fin.walley.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fin.walley.dto.finance.AccountDto;
import org.fin.walley.dto.user.UserDto;
import org.fin.walley.service.finance.AccountService;
import org.fin.walley.service.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @GetMapping
    public String listAccounts(@AuthenticationPrincipal UserDetails principal,
                               Model model) {
        UserDto user = getCurrentUser(principal);
        List<AccountDto> accounts = accountService.getAllAccounts(user.getId());
        model.addAttribute("accounts", accounts);
        return "accounts/list"; // templates/accounts/list.html
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("account")) {
            model.addAttribute("account", new AccountDto());
        }
        return "accounts/form"; // templates/accounts/form.html
    }

    @PostMapping
    public String createAccount(@AuthenticationPrincipal UserDetails principal,
                                @Valid @ModelAttribute("account") AccountDto form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        UserDto user = getCurrentUser(principal);

        if (bindingResult.hasErrors()) {
            return "accounts/form";
        }

        accountService.createAccount(user.getId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Счёт создан.");
        return "redirect:/accounts";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable("id") Long id,
                               Model model) {
        UserDto user = getCurrentUser(principal);

        if (!model.containsAttribute("account")) {
            AccountDto account = accountService.getAccountById(user.getId(), id)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
            model.addAttribute("account", account);
        }

        return "accounts/form";
    }

    @PostMapping("/{id}")
    public String updateAccount(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable("id") Long id,
                                @Valid @ModelAttribute("account") AccountDto form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        UserDto user = getCurrentUser(principal);

        if (bindingResult.hasErrors()) {
            return "accounts/form";
        }

        accountService.updateAccount(user.getId(), id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Счёт обновлён.");
        return "redirect:/accounts";
    }

    @PostMapping("/{id}/archive")
    public String archiveAccount(@AuthenticationPrincipal UserDetails principal,
                                 @PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        UserDto user = getCurrentUser(principal);
        accountService.archiveAccount(user.getId(), id);
        redirectAttributes.addFlashAttribute("successMessage", "Счёт архивирован.");
        return "redirect:/accounts";
    }

    @PostMapping("/{id}/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        UserDto user = getCurrentUser(principal);
        try {
            accountService.deleteAccount(user.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "Счёт удалён.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/accounts";
    }

    private UserDto getCurrentUser(UserDetails principal) {
        return userService.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.getUsername()));
    }
}
