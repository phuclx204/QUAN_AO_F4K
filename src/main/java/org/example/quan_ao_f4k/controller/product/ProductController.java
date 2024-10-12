package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Category;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.repository.product.CategoryRepository;
import org.example.quan_ao_f4k.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "admin/products")
@Controller
@AllArgsConstructor
public class ProductController {
	private final ProductService productService;

	@GetMapping
	public String getProduct(Model model) {
		return "admin/product/products";
	}


	// Lấy danh sách với phân trang và sắp xếp
	@GetMapping("/list")
	public ResponseEntity<ListResponse<ProductResponse>> getAllBrands(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<ProductResponse> response = productService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}


	// Tạo mới
	@PostMapping
	public ResponseEntity<?> add(@RequestBody ProductRequest brandRequest) {
		ProductResponse newBrand = productService.save(brandRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
	}

	// Cập nhật thông tin
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductRequest request) {
		return ResponseEntity.ok(productService.save(id, request));
	}



	// Cập nhật trạng thái
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody ProductRequest request) {
		productService.updateStatus(id, request.getStatus());
		return ResponseEntity.noContent().build();
	}


	// Tìm theo tên
	@GetMapping("/search")
	public ResponseEntity<ProductResponse> getByName(@RequestParam String name) {
		ProductResponse response = productService.findByName(name);
		return ResponseEntity.ok(response);
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
}
