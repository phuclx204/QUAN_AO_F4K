package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.service.product.ProductService;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping(value = "${api.prefix}/admin/products")
@RestController
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ImageRepository imageRepository;


    // Lấy danh sách với phân trang và sắp xếp
    @GetMapping("/list")
    public ResponseEntity<ListResponse<ProductResponse>> getAllBrands(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search) {
        ListResponse<ProductResponse> response = productService.findAll(page, size, sort, filter, search, false);
        response.getContent().stream()
                .forEach(el -> {
                    Image optionalImage = imageRepository.findImageByIdParent(el.getId(), F4KConstants.TableCode.PRODUCT);
                    if (optionalImage != null) {
                        el.setPathImg(optionalImage.getFileUrl());
                    }
                });
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> add(@Valid @ModelAttribute ProductRequest productRequest, BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        boolean exists = productService.isAddExistProductByBrandAndCate(productRequest.getName(),
                productRequest.getBrandId(), productRequest.getCategoryId());

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Sản phẩm có tên với thương hiệu và danh mục này đã tồn tại");
        }

        productRequest.setSlug(F4KUtils.toSlug(productRequest.getName()));
        ProductResponse productResponse = productService.save(productRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @ModelAttribute ProductRequest productRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        boolean exists = productService.isUpdateExistProductByBrandAndCate(productRequest.getName(),
                productRequest.getBrandId(), productRequest.getCategoryId(), id);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).
                    body("Sản phẩm có tên với thương hiệu và danh mục này đã tồn tại.");
        }
        ProductResponse productResponse = productService.save(id, productRequest);
        return ResponseEntity.ok(productResponse);
    }

    // Cập nhật trạng thái
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id, @RequestBody ProductRequest request) {
        productService.updateStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    // Xuất danh sách thương hiệu ra Excel
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws Exception {
        productService.exportExcel(response);
    }

    // Xuất danh sách thương hiệu ra PDF
    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws Exception {
        productService.exportPdf(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
