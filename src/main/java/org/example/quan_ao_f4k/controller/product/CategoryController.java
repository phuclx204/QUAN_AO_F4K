package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.product.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/admin/category")
@Controller
@AllArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	// Trả về giao diện HTML cho danh sách thương hiệu
	@GetMapping
	public String getAllBrandsPage() {
		return "/admin/product/category"; // Tên file HTML
	}

	// Lấy danh sách thương hiệu với phân trang và sắp xếp
	@GetMapping("/list")
	public ResponseEntity<ListResponse<CategoryResponse>> getAllBrands(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<CategoryResponse> response = categoryService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}


	// Tạo mới thương hiệu
	@PostMapping
	public ResponseEntity<?> addBrand(@RequestBody CategoryRequest brandRequest) {
		if (categoryService.existsByName(brandRequest.getName())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Thương hiệu với tên này đã tồn tại!");
		}

		CategoryResponse newBrand = categoryService.save(brandRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
	}

	// Cập nhật thông tin thương hiệu
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody CategoryRequest request) {
		if (categoryService.existsByNameAndIdNot(request.getName(), id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên đã tồn tại");
		}
		return ResponseEntity.ok(categoryService.save(id, request));
	}



	// Cập nhật trạng thái thương hiệu
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBrandStatus(@PathVariable Long id, @RequestBody CategoryRequest request) {
		categoryService.updateStatus(id, request.getStatus());
		return ResponseEntity.noContent().build();
	}


	// Tìm thương hiệu theo tên
	@GetMapping("/search")
	public ResponseEntity<CategoryResponse> getBrandByName(@RequestParam String name) {
		CategoryResponse response = categoryService.findByName(name);
		return ResponseEntity.ok(response);
	}

	// Xuất danh sách thương hiệu ra Excel
	@GetMapping("/export/excel")
	public void exportToExcel(HttpServletResponse response) throws Exception {
		categoryService.exportExcel(response);
	}

	// Xuất danh sách thương hiệu ra PDF
	@GetMapping("/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws Exception {
		categoryService.exportPdf(response);
	}

	@GetMapping("/active")
	public ResponseEntity<List<CategoryResponse>> getActiveCategory() {
		List<CategoryResponse> active = categoryService.findByStatusActive();
		return ResponseEntity.ok(active);
	}
}
