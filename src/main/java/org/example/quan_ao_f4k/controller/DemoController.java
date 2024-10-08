package org.example.quan_ao_f4k.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "admin")
public class DemoController {

    @GetMapping(value = "statistical")
    public String mmDashboard() {
        return "admin/index";
    }
}