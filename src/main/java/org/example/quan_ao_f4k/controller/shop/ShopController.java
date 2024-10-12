package org.example.quan_ao_f4k.controller.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("shop")
public class ShopController {

    @GetMapping("index")
    public String index() {
        return "shop/pages/index";
    }

    @GetMapping("cart")
    public String cart() {
        return "shop/pages/cart";
    }

    @GetMapping("category")
    public String category() {
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
