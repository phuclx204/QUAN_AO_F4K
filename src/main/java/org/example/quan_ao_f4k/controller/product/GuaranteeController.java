package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.dto.request.product.GuaranteeRequest;
import org.example.quan_ao_f4k.dto.response.product.GuaranteeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.product.GuaranteeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/guarantee")
@AllArgsConstructor
public class GuaranteeController extends GenericController<GuaranteeRequest, GuaranteeResponse> {

	private final GuaranteeService brandService;

	// Trả về giao diện HTML cho danh sách thương hiệu
	@GetMapping
	public String getAllBrandsPage() {
		return "/admin/product/guarantee"; // Tên file HTML
	}

	// Lấy danh sách thương hiệu với phân trang và sắp xếp
	@GetMapping("/list")
	public ResponseEntity<ListResponse<GuaranteeResponse>> getAllBrands(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<GuaranteeResponse> response = brandService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}


	// Tạo mới thương hiệu
	@PostMapping
	public ResponseEntity<?> addBrand(@RequestBody GuaranteeRequest brandRequest) {
		if (brandService.existsByName(brandRequest.getName())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên này đã tồn tại!");
		}

		GuaranteeResponse newBrand = brandService.save(brandRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
	}

	// Cập nhật thông tin thương hiệu
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody GuaranteeRequest request) {
		if (brandService.existsByNameAndIdNot(request.getName(), id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên đã tồn tại");
		}
		return ResponseEntity.ok(brandService.save(id, request));
	}



	// Cập nhật trạng thái thương hiệu
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBrandStatus(@PathVariable Long id, @RequestBody GuaranteeRequest request) {
		brandService.updateStatus(id, request.getStatus());
		return ResponseEntity.noContent().build();
	}


	// Tìm thương hiệu theo tên
	@GetMapping("/search")
	public ResponseEntity<GuaranteeResponse> getBrandByName(@RequestParam String name) {
		GuaranteeResponse response = brandService.findByName(name);
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
}
