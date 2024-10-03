package org.example.quan_ao_f4k.controller;

import jakarta.servlet.http.HttpSession;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminServiceImpl adminService;

    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam(value = "verifyCode", required = false) String verifyCode,
                        HttpSession session) {
//        if (!StringUtils.hasText(verifyCode)) {
//            session.setAttribute("errorMsg", "Mã xác minh không được để trống");
//            return "admin/login";
//        }
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
            session.setAttribute("errorMsg", "Tên người dùng hoặc mật khẩu không thể trống");
            return "admin/login";
        }
//        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
//        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
//            session.setAttribute("errorMsg", "Lỗi mã xác minh");
//            return "admin/login";
//        }
        User adminUser = adminService.login(userName, password);
        if (adminUser != null) {
            session.setAttribute("loginUser", adminUser.getUsername());
            session.setAttribute("loginUserId", adminUser.getId());
            //session.setMaxInactiveInterval(60 * 60 * 2) - hiệu lực session là 2h;
            return "redirect:/admin/dashboard";
        } else {
            session.setAttribute("errorMsg", "Đăng nhập không thành công");
            return "admin/login";
        }
    }

    @GetMapping(value = "dashboard")
    public String mmDashboard(Model model) {
        model.addAttribute("demo", "BLAAAA");
        return "admin/layout";
    }
}
