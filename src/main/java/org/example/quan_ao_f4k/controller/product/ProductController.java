package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.product.ProductDetailService;
import org.example.quan_ao_f4k.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RequestMapping(value = "/admin/products")
@Controller
@AllArgsConstructor
public class ProductController {
	private final ProductService productService;

	@GetMapping()
	public String getProduct() {
		return "admin/product/products";
	}


	// Lấy danh sách với phân trang và sắp xếp
	@GetMapping("/list")
	public ResponseEntity<ListResponse<ProductResponse>> getAllBrands(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "id,desc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<ProductResponse> response = productService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> add(@Valid @ModelAttribute ProductRequest productRequest, BindingResult bindingResult) {
		try {
			if (bindingResult.hasErrors()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
			}

			boolean exists = productService.isAddExistProductByBrandAndCate(productRequest.getName(),
					productRequest.getBrandId(), productRequest.getCategoryId());

			if (exists) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("Sản phẩm có tên với thương hiệu và danh mục này đã tồn tại");
			}

			String fileName = uploadThumbnail(productRequest.getThumbnail());
			productRequest.setThumbnailName(fileName);
			ProductResponse productResponse = productService.save(productRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu file");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long id,
	                                @Valid @ModelAttribute ProductRequest productRequest,
	                                BindingResult bindingResult) {
		try {
			if (bindingResult.hasErrors()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
			}
			boolean exists = productService.isUpdateExistProductByBrandAndCate(productRequest.getName(),
					productRequest.getBrandId(), productRequest.getCategoryId(),id);

			if (exists) {
				return ResponseEntity.status(HttpStatus.CONFLICT).
						body("Sản phẩm có tên với thương hiệu và danh mục này đã tồn tại.");
			}
			String fileName = uploadThumbnail(productRequest.getThumbnail());
			productRequest.setThumbnailName(fileName);
			ProductResponse productResponse = productService.save(id, productRequest);
			return ResponseEntity.ok(productResponse);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu file");
		}
	}


	// Cập nhật trạng thái
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id, @RequestBody ProductRequest request) {
		productService.updateStatus(id, request.getStatus());
		return ResponseEntity.noContent().build();
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

	private String uploadThumbnail(MultipartFile thumbnail) throws IOException {
		String uploadDir = "src/main/resources/static/admin/img/";
		Path path = Paths.get(uploadDir);

		if (thumbnail != null && !thumbnail.isEmpty()) {
			String fileName = thumbnail.getOriginalFilename();
			Files.copy(thumbnail.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		}
		return "people.png";
	}

}
