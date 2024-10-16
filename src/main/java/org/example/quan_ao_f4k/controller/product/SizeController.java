package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.SizeRequest;
import org.example.quan_ao_f4k.dto.response.product.SizeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.product.SizeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "id,desc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<SizeResponse> response = sizeService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}


	// Tạo mới thương hiệu
	@PostMapping
	public ResponseEntity<?> addBrand(@Valid @RequestBody SizeRequest sizeRequest, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
		}
		if (sizeService.existsByName(sizeRequest.getName())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Tên đã tồn tại!");
		}
		SizeResponse newBrand = sizeService.save(sizeRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(newBrand);
	}

	// Cập nhật thông tin thương hiệu
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBrand(@PathVariable Long id,@Valid @RequestBody SizeRequest request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
		}
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

	@GetMapping("/active")
	public ResponseEntity<List<SizeResponse>> getActiveBrands() {
		List<SizeResponse> activeBrands = sizeService.findByStatusActive();
		return ResponseEntity.ok(activeBrands);
	}
}
