package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.SizeRequest;
import org.example.quan_ao_f4k.dto.response.product.SizeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.product.SizeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/admin/size")
@Controller
@AllArgsConstructor
public class SizeController {
	private SizeService sizeService;

	@GetMapping
	public String getAllBrandsPage() {
		return "/admin/product/size";
	}

	// Lấy danh sách , phân trang và sắp xếp
	@GetMapping("/list")
	public ResponseEntity<ListResponse<SizeResponse>> getAllBrands(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id,asc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<SizeResponse> response = sizeService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}


	// Tạo mới thương hiệu
	@PostMapping
	public ResponseEntity<?> addBrand(@RequestBody SizeRequest sizeRequest) {
		// Kiểm tra xem thương hiệu đã tồn tại chưa
		if (sizeService.existsByName(sizeRequest.getName())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên đã tồn tại!");
		}

		SizeResponse newBrand = sizeService.save(sizeRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
	}

	// Cập nhật thông tin thương hiệu
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBrand(@PathVariable Long id, @RequestBody SizeRequest request) {
		if (sizeService.existsByNameAndIdNot(request.getName(), id)) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên đã tồn tại");
		}
		return ResponseEntity.ok(sizeService.save(id, request));
	}



	// Cập nhật trạng thái thương hiệu
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBrandStatus(@PathVariable Long id, @RequestBody SizeRequest request) {
		sizeService.updateStatus(id, request.getStatus());
		return ResponseEntity.noContent().build();
	}


	// Tìm thương hiệu theo tên
	@GetMapping("/search")
	public ResponseEntity<SizeResponse> getBrandByName(@RequestParam String name) {
		SizeResponse response = sizeService.findByName(name);
		return ResponseEntity.ok(response);
	}

	// Xuất danh sách thương hiệu ra Excel
	@GetMapping("/export/excel")
	public void exportToExcel(HttpServletResponse response) throws Exception {
		sizeService.exportExcel(response);
	}

	// Xuất danh sách thương hiệu ra PDF
	@GetMapping("/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws Exception {
		sizeService.exportPdf(response);
	}
}
