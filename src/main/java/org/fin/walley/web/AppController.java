package org.fin.walley.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
public class AppController {

    @GetMapping("/dashboard")
    public String dashboard() {
        // шаблон templates/app/dashboard.html
        return "app/dashboard";
    }
}