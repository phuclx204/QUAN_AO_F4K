package org.example.quan_ao_f4k.controller.shop;

import org.example.quan_ao_f4k.service.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("shop")
public class ShopController {

    @Autowired
    private ShopService service;

    @GetMapping("home")
    public String index() {
        return "shop/pages/index";
    }

    @GetMapping("cart")
    public String cart() {
        return "shop/pages/cart";
    }

    @GetMapping("category")
    public String category(Model model) {
        service.addModelFilter(model);
        return "shop/pages/category";
    }

    @GetMapping("checkout")
    public String checkout() {
        return "shop/pages/checkout";
    }

    @GetMapping("product")
    public String product() {
        return "shop/pages/product";
    }
}
