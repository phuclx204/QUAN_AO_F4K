package org.example.quan_ao_f4k.controller.shop;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.service.shop.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Page<ObjectNode> listProduct(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size) {
        return service.getListProductDetail(page, size);
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
