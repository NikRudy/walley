package org.fin.walley.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/panel")
    public String adminPanel() {
        // шаблон templates/admin/panel.html
        return "admin/panel";
    }
}