package org.example.quan_ao_f4k.controller.shop;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.quan_ao_f4k.dto.request.shop.ShopRequest;
import org.example.quan_ao_f4k.service.shop.ShopProductService;
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
    private ShopProductService shopProductService;

    // home
    @GetMapping("/home")
    public String index() {
        System.out.println("vao");
        return "/shop/pages/index";
    }

    // product list
    @GetMapping("/collections")
    public String category(Model model) {
        shopProductService.addModelFilter(model);
        return "/shop/pages/product-list";
    }

    @GetMapping("/collections/list-product")
    @ResponseBody
    public ResponseEntity<Page<ObjectNode>> listProductDetail(@ModelAttribute ShopRequest.RequestSearch requestSearch) {
        return ResponseEntity.status(HttpStatus.OK).body(shopProductService.getListProductDetail(requestSearch));
    }

    // detail product
    @GetMapping("/product/{id}")
    public String product(@PathVariable String id) {
        System.out.println(id);
        return "/shop/pages/product-detail";
    }

    // checkout
    @GetMapping("/checkout")
    public String checkout() {
        return "/shop/pages/checkout";
    }
}