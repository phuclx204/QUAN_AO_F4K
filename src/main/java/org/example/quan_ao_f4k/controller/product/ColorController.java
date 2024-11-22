package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.dto.request.product.ColorRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.dto.response.product.ColorResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.product.ColorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/color")
@AllArgsConstructor
public class ColorController extends GenericController<ColorRequest, ColorResponse> {

    private final ColorService brandService;

    // Lấy danh sách thương hiệu với phân trang và sắp xếp
    @GetMapping("/list")
    public ResponseEntity<ListResponse<ColorResponse>> getAllBrands(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,desc") String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search) {
        ListResponse<ColorResponse> response = brandService.findAll(page, size, sort, filter, search, false);
        return ResponseEntity.ok(response);
    }


    // Tạo mới thương hiệu
    @PostMapping
    public ResponseEntity<?> addBrand(@Valid @RequestBody ColorRequest brandRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        if (brandService.existsByName(brandRequest.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Tên này đã tồn tại!");
        }

        ColorResponse newBrand = brandService.save(brandRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
    }

    // Cập nhật thông tin thương hiệu
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Long id,@Valid @RequestBody ColorRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        if (brandService.existsByNameAndIdNot(request.getName(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Tên đã tồn tại");
        }
        return ResponseEntity.ok(brandService.save(id, request));
    }



    // Cập nhật trạng thái thương hiệu
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBrandStatus(@PathVariable Long id, @RequestBody ColorRequest request) {
        brandService.updateStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }


    // Xuất danh sách thương hiệu ra Excel
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws Exception {
        brandService.exportExcel(response);
    }

    // Xuất danh sách thương hiệu ra PDF
    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws Exception {
        brandService.exportPdf(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ColorResponse>> getActiveBrands() {
        List<ColorResponse> activeBrands = brandService.findByStatusActive();
        return ResponseEntity.ok(activeBrands);
    }
}
