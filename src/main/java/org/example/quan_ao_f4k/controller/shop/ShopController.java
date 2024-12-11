package org.example.quan_ao_f4k.controller.shop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.mapper.shop.ShopProductMapper;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.service.shop.ShopCartService;
import org.example.quan_ao_f4k.service.shop.ShopCheckOutService;
import org.example.quan_ao_f4k.service.shop.ShopProductService;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.HoaDonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/shop")
@Slf4j
@AllArgsConstructor
public class ShopController {
    private final ShopProductService shopProductService;
    private final ShopCheckOutService shopCheckOutService;
    private final ShopCartService shopCartService;
    private final PasswordEncoder passwordEncoder;

    private final F4KUtils f4KUtils;
    private final ShopProductMapper shopProductMapper;

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;

    // home
    @GetMapping("/home")
    public String index(Model model) {
        shopProductService.addModelHome(model);
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

    @GetMapping("/product/update-quantity/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @RequestParam(value = "quantity", required = false) Integer quantity) {
        shopCartService.updateQuantity(id, quantity);
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

    @GetMapping("/purchase-history-detail/{code}")
    public String purchaseHistoryDetail(@PathVariable String code, Model model) {
        try {
            shopCheckOutService.addModalPurchaseHistoryDetail(model, code);
        } catch (NoSuchElementException e) {
            return "/shop/error/404";
        }

        return "/shop/pages/purchase-history-detail";
    }

    @GetMapping("/create-order")
    public String createOrder(
            @RequestParam("buyType") HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang
            , @RequestParam("note") String note
            , RedirectAttributes redirectAttributes) {
        shopCheckOutService.createOneOrder(phuongThucMuaHang, note, true);
        redirectAttributes.addFlashAttribute("createOrderSuccess", true);
        return "redirect:/shop/purchase-history";
    }

    @GetMapping("/cancel-order")
    public ResponseEntity<?> cancelOrder(@RequestParam("orderId") Long orderId, @RequestParam("note") String note) {
        shopCheckOutService.cancelOrder(orderId, note);
        return ResponseEntity.ok().build();
    }

    // Promotion
    @GetMapping("/list-promotion")
    public ResponseEntity<List<Promotion>> getListPromotion() {
        return ResponseEntity.ok(shopProductService.getListPromotion());
    }

    @GetMapping("/promotion/{id}")
    public String detailPromotion(@PathVariable Long id, Model model) {
        shopProductService.addModelPromotion(model, id);
        return "/shop/pages/promotion-detail";
    }

    // Account setting
    @GetMapping("/account-setting")
    public String account(Model model) {
        model.addAttribute("userInfo", f4KUtils.getUser());
        return "/shop/pages/account-setting";
    }

    @GetMapping("/account-setting/get-info")
    public ResponseEntity<?> getUserInfo(@Param("id") Long id, @RequestParam("username") String username) {
        User user = userRepository.findByIdAndUsername(id, username).orElse(null);
        return ResponseEntity.ok(shopProductMapper.toUserDto(user));
    }

    @PutMapping("/account-setting/update-info")
    public ResponseEntity<?> updateUserInfo(@RequestBody ShopProductResponse.UserDto userDto) {
        User user = userRepository.findByIdAndUsername(userDto.getId(), userDto.getUsername()).orElseThrow(
                () -> new BadRequestException("Dữ liệu không phù hợp")
        );

        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setNumberPhone(userDto.getNumberPhone());
        user.setAddressDetail(userDto.getAddressDetail());
        user.setGender(userDto.getGender());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/account-setting/update-password/{id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        User user = userRepository.findByIdUser(id).orElseThrow(
                () -> new BadRequestException("Không tìm thấy tài khoản người dùng")
        );

        String passwordNew = passwordEncoder.encode(newPassword);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không đúng");
        } else {
            user.setPassword(passwordNew);
            userRepository.save(user);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/list-brand")
    public ResponseEntity<?> getListBrand() {
        List<Brand> lstBrand = brandRepository.findByStatus(F4KConstants.STATUS_ON);
        return ResponseEntity.ok(lstBrand);
    }
}