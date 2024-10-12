package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.service.product.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/brand")
@AllArgsConstructor
public class BrandController extends GenericController<BrandRequest, BrandResponse> {

	private final BrandService brandService;

	// Trả về giao diện HTML cho danh sách thương hiệu
	@GetMapping
	public String getAllBrandsPage() {
		return "/admin/product/brand"; // Tên file HTML
	}

	// Lấy danh sách thương hiệu với phân trang và sắp xếp
	@GetMapping("/list")
	public ResponseEntity<ListResponse<BrandResponse>> getAllBrands(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<BrandResponse> response = brandService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}


	// Tạo mới thương hiệu
	@PostMapping
	public ResponseEntity<?> addBrand(@RequestBody BrandRequest brandRequest) {
		if (brandService.existsByName(brandRequest.getName())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên này đã tồn tại!");
		}

		BrandResponse newBrand = brandService.save(brandRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
	}

	// Cập nhật thông tin thương hiệu
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
		// Kiểm tra xem tên thương hiệu đã tồn tại chưa
		System.out.println("ID Brand "+id);
		if (brandService.existsByNameAndIdNot(request.getName(), id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên đã tồn tại");
		}
		return ResponseEntity.ok(brandService.save(id, request));
	}



	// Cập nhật trạng thái thương hiệu
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBrandStatus(@PathVariable Long id, @RequestBody BrandRequest request) {
		brandService.updateStatus(id, request.getStatus());
		return ResponseEntity.noContent().build();
	}


	// Tìm thương hiệu theo tên
	@GetMapping("/search")
	public ResponseEntity<BrandResponse> getBrandByName(@RequestParam String name) {
		BrandResponse response = brandService.findByName(name);
		return ResponseEntity.ok(response);
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
	public ResponseEntity<List<BrandResponse>> getActiveBrands() {
		List<BrandResponse> activeBrands = brandService.findByStatusActive();
		return ResponseEntity.ok(activeBrands);
	}

}
