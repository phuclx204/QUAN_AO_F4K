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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RequestMapping(value = "admin/products")
@Controller
@AllArgsConstructor
public class ProductController {
	private final ProductService productService;

	@GetMapping({"", "/"})
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


	//	thêm
	@PostMapping
	public ResponseEntity<?> add(@ModelAttribute ProductRequest productRequest) {
		MultipartFile thumbnail = productRequest.getThumbnail();
		String fileName = "people.png";  // Mặc định nếu không có file
		String uploadDir = "src/main/resources/static/admin/img/";
		Path path = Paths.get(uploadDir);

		try {
			if (thumbnail != null && !thumbnail.isEmpty()) {
				fileName = thumbnail.getOriginalFilename();
				Files.copy(thumbnail.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu file");
		}

		// Gán tên file đã upload vào request
		productRequest.setThumbnailName(fileName);

		ProductResponse productResponse = productService.save(productRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(
			@PathVariable("id") Long id,
			@ModelAttribute ProductRequest productRequest) {

		MultipartFile thumbnail = productRequest.getThumbnail();
		ProductResponse res = productService.findById(id);

		String fileName = res.getThumbnail();
		String uploadDir = "src/main/resources/static/admin/img/";
		Path path = Paths.get(uploadDir);

		try {
			if (thumbnail != null && !thumbnail.isEmpty()) {
				fileName = thumbnail.getOriginalFilename();
				Files.copy(thumbnail.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu file");
		}

		productRequest.setThumbnailName(fileName);
		ProductResponse productResponse = productService.save(id, productRequest);
		return ResponseEntity.ok(productResponse);
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
}
