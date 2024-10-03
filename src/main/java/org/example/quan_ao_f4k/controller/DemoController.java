package org.example.quan_ao_f4k.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "pages")
public class DemoController {

    @GetMapping(value = "dashboard")
    public String mmDashboard(Model model) {
        model.addAttribute("demo", "BLAAAA");
        return "admin/layout";
    }

    @GetMapping(value = "demo")
    public String demo(Model model) {
        model.addAttribute("demo", "FUCKYOUUU");
        return "admin/login";
    }
}
