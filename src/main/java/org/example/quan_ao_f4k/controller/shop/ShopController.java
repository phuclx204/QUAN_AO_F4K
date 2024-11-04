package org.example.quan_ao_f4k.controller.shop;

import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
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
    public ResponseEntity<Page<ShopProductResponse.ProductDetailDto>> listProductDetail(@ModelAttribute ShopProductRequest.RequestSearch requestSearch) {
        return ResponseEntity.status(HttpStatus.OK).body(shopProductService.searchProducts(requestSearch));
    }

    //    // detail product
    @GetMapping("/product/{slug}")
    public String product(@PathVariable String slug, Model model
            , @RequestParam(value = "color", required = false) String colorHex
            , @RequestParam(value = "size", required = false) String sizeName) {
        shopProductService.addModelProductDetail(model, slug, colorHex, sizeName);
        return "/shop/pages/product-detail";
    }


//
//    // checkout
//    @GetMapping("/checkout")
//    public String checkout() {
//        return "/shop/pages/checkout";
//    }
//
//    // addCart
//    @GetMapping("/cart")
//    public String showCart(Model model) {
//        return "/shop/pages/cart";
//    }
//
//    @GetMapping("/cart/list-cart")
//    public ResponseEntity<ShopResponse.CartResponse> getListCart(@AuthenticationPrincipal UserDetails user) {
//        return ResponseEntity.ok(shopProductService.getListCart(user));
//    }
//
//    @GetMapping("/product/add-cart/{id}")
//    public String addCart(@PathVariable Long id, @AuthenticationPrincipal User user) {
//        shopProductService.addCart(id, user);
//        return "redirect:/shop/pages/add-cart";
//    }
}