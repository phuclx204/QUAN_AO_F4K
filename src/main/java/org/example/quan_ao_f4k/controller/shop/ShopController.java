package org.example.quan_ao_f4k.controller.shop;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.quan_ao_f4k.dto.request.shop.ShopRequest;
import org.example.quan_ao_f4k.service.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("list-product")
    @ResponseBody
    public ResponseEntity<Page<ObjectNode>> listProduct(@ModelAttribute ShopRequest.RequestSearch requestSearch) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getListProductDetail(requestSearch));
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
