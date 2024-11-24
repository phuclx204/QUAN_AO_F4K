package org.example.quan_ao_f4k.controller.product;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.service.product.ProductDetailService;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@RequestMapping(value = "/admin/products/product-detail")
@AllArgsConstructor
@Controller
public class ProductDetailController {

    private final ProductDetailService productDetailService;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    @GetMapping("list")
    public ResponseEntity<ListResponse<ProductDetailResponse>> getAllBrands(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search) {
        ListResponse<ProductDetailResponse> response = productDetailService.findAll(page, size, sort, filter, search, false);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public String getProductDetails(@PathVariable("productId") Long productId, Model model) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            model.addAttribute("productId", product.get().getId());
            model.addAttribute("productName", product.get().getName());
            return "admin/product/product-detail-list";
        } else {
            return "error/error_404";
        }
    }

    @GetMapping
    public ResponseEntity<?> getDetail(@RequestParam("id") Long id) {
        return ResponseEntity.ok(productDetailService.findById(id));
    }

    //	product detail
    @GetMapping("/{productId}/list")
    public ResponseEntity<?> getProductDetails(@PathVariable("productId") Long productId,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "5") int size,
                                               @RequestParam(defaultValue = "id,desc") String sort,
                                               @RequestParam(required = false) String filter,
                                               @RequestParam(required = false) String search) {
        ListResponse<ProductDetailResponse> response =
                productDetailService.getProductDetailByProductId(productId, page, size, sort, filter, search, false);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{productId}/add")
    public ResponseEntity<?> addProductDetail(@PathVariable("productId") Long productId,
                                              @Valid @ModelAttribute ProductDetailRequest productDetailRequest,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(productDetailService.addProductDetail(productId, productDetailRequest));
    }

    @PutMapping("/{productId}/update/{id}")
    public ResponseEntity<?> updateProductDetail(@PathVariable("productId") Long productId,
                                                 @PathVariable("id") Long id,
                                                 @Valid @ModelAttribute ProductDetailRequest productDetailRequest,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(productDetailService.updateProductDetail(productId, id, productDetailRequest));
    }

    @DeleteMapping("/{productId}/delete/{id}")
    public ResponseEntity<?> deleteProductDetail(@PathVariable("productId") Long productId,
                                                 @PathVariable("id") Long id) {
        boolean deleted = productDetailService.deleteProductDetail(productId, id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Sản phẩm này đã ràng buộc với đối tượng khác không thể xóa");
        }
        return ResponseEntity.ok("Chi tiết sản phẩm đã được xóa thành công");
    }

    @GetMapping("/{productId}/update-status/{id}")
    public ResponseEntity<?> updateStatusProductDetail(@PathVariable("productId") Long productId,
                                                 @PathVariable("id") Long id,
                                                 @RequestParam("status") Integer status) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            ProductDetail productDetail = productDetailRepository.findById(id).orElseThrow(
                    () -> new BadRequestException(F4KConstants.ErrCode.NOT_FOUND, id, F4KConstants.TableCode.PRODUCT_DETAIL)
            );
            if (status != F4KConstants.STATUS_ON && status != F4KConstants.STATUS_OFF) status = F4KConstants.STATUS_OFF;
            productDetail.setStatus(status);
            productDetailRepository.save(productDetail);
            return ResponseEntity.ok().build();
        } else {
            throw new BadRequestException(F4KConstants.ErrCode.NOT_FOUND, productId, F4KConstants.TableCode.PRODUCT);
        }
    }

    @GetMapping("/{productId}/create")
    public String createProductDetail(@PathVariable Long productId, Model model) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            model.addAttribute("productId", product.get().getId());
            model.addAttribute("productName", product.get().getName());
            return "admin/product/product-detail-add";
        } else {
            return "error/error_404";
        }
    }
}
