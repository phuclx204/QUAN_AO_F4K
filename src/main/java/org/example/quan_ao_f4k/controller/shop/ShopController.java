package org.example.quan_ao_f4k.controller.shop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.service.shop.ShopCartService;
import org.example.quan_ao_f4k.service.shop.ShopCheckOutService;
import org.example.quan_ao_f4k.service.shop.ShopProductService;
import org.example.quan_ao_f4k.util.HoaDonUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shop")
@Slf4j
@AllArgsConstructor
public class ShopController {
    private final ShopProductService shopProductService;
    private final ShopCheckOutService shopCheckOutService;
    private final ShopCartService shopCartService;

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

    // detail product
    @GetMapping("/product/{slug}")
    public String product(@PathVariable String slug, Model model
            , @RequestParam(value = "color", required = false) String colorHex
            , @RequestParam(value = "size", required = false) String sizeName) {
        try {
            shopProductService.addModelProductDetail(model, slug, colorHex, sizeName);
        } catch (Exception e) {
            log.error("Có lỗi trong quá trình tìm kiếm sản phẩm: {}", e.getMessage());
            return "/shop/error/404";
        }
        return "/shop/pages/product-detail";
    }

    // cart
    @GetMapping("/cart")
    public String showCart(Model model) {
        shopCartService.addModelCart(model);
        return "/shop/pages/cart";
    }

    @GetMapping("/cart/list-cart")
    public ResponseEntity<ShopProductResponse.CartResponse> getListCart(@RequestParam("username") String username) {
        return ResponseEntity.ok(shopCartService.getListCart(username));
    }

    @PostMapping("/product/add-cart/{id}")
    public ResponseEntity<?> addCart(@PathVariable Long id
            , @RequestParam(value = "quantity", required = false) Integer quantity
            , @RequestParam(value = "color", required = false) Integer color
            , @RequestParam(value = "size", required = false) Integer size) {
        return ResponseEntity.ok(shopCartService.addCart(id, quantity));
    }

    @PostMapping("/product/remove-cart/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Long id) {
        shopCartService.removeCart(id);
        return ResponseEntity.ok().build();
    }

    // checkout
    @GetMapping("/checkout")
    public String checkout(Model model) {
        shopCheckOutService.addModelCheckout(model);
        return "/shop/pages/checkout";
    }

    @GetMapping("/get-detail-shipping-info")
    public ResponseEntity<ShopProductResponse.ShippingInfoDto> detailShippingInfo(@RequestParam("shippingId") Long shippingId) {
        return ResponseEntity.ok(shopCartService.detailShippingInfo(shippingId));
    }

    @GetMapping("/get-shipping-info")
    public ResponseEntity<?> getShippingInfo() {
        return ResponseEntity.ok(shopCartService.getShippingInfo());
    }

    @GetMapping("/get-fee")
    @ResponseBody
    public String getFee(@RequestParam("districtId") Integer districtId, @RequestParam("wardCode") String wardCode) {
        return shopCartService.getFree(districtId, wardCode);
    }

    @GetMapping("/get-province")
    @ResponseBody
    public ResponseEntity<?> getProvince() {
        return ResponseEntity.ok(shopCartService.getProvince());
    }

    @GetMapping("/get-district")
    @ResponseBody
    public ResponseEntity<?> getDistrict(@RequestParam("provinceId") Integer provinceId) {
        return ResponseEntity.ok(shopCartService.getDistrict(provinceId));
    }

    @GetMapping("/get-ward")
    @ResponseBody
    public ResponseEntity<?> getWard(@RequestParam("districtId") Integer districtId) {
        return ResponseEntity.ok(shopCartService.getWard(districtId));
    }

    @PostMapping("/add-shipping-info")
    public ResponseEntity<Void> addShippingInfo(@RequestBody ShopProductResponse.ShippingInfoDto shippingInfoDto) {
        shopCartService.addShippingInfo(shippingInfoDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/set-default-shipping-info")
    public ResponseEntity<Void> setDefaultAddress(@RequestParam("shippingId") Long shippingId) {
        shopCartService.setDefaultAddress(shippingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/purchase-history")
    public String purchaseHistory(Model model) {
        shopCheckOutService.addModalPurchaseHistory(model);
        return "/shop/pages/purchase-history";
    }

    @GetMapping("/create-order")
    public String createOrder(@RequestParam("buyType") HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang, RedirectAttributes redirectAttributes) {
        shopCheckOutService.createOrder(phuongThucMuaHang);
        redirectAttributes.addFlashAttribute("createOrderSuccess", true);
        return "redirect:/shop/purchase-history";
    }
}