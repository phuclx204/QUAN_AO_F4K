package org.example.quan_ao_f4k.controller.product;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.service.product.ProductDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping(value = "/admin/products/product-detail")
@AllArgsConstructor
@Controller
public class ProductDetailController {

	private final ProductDetailService productDetailService;
	private final ProductRepository productRepository;


	@GetMapping("/{productId}")
	public String getProductDetails(@PathVariable("productId") Long productId, Model model) {
		model.addAttribute("productId", productId);
		return "admin/product/product-detail";

	}

	//	product detail
	@GetMapping("/{productId}/list")
	public ResponseEntity<?> getProductDetails(@PathVariable("productId") Long productId,
	                                           @RequestParam(defaultValue = "1") int page,
	                                           @RequestParam(defaultValue = "5") int size,
	                                           @RequestParam(defaultValue = "id,desc") String sort,
	                                           @RequestParam(required = false) String filter,
	                                           @RequestParam(required = false) String search) {
		ListResponse<ProductDetailResponse> response =
				productDetailService.getProductDetailByProductId(productId, page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}


	@PostMapping("/{productId}/add")
	public ResponseEntity<?> addProductDetail(@PathVariable("productId") Long productId,
	                                          @Valid @RequestBody ProductDetailRequest productDetailRequest,
	                                          BindingResult bindingResult) {
		boolean exists = productDetailService.isAddExistsByProductSizeAndColor(productId,
				productDetailRequest.getSizeId(), productDetailRequest.getColorId());
		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
		}
		if (exists) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Sản phẩm có màu và kích cỡ này đã tồn tại");
		}
		return ResponseEntity.ok(productDetailService.addProductDetail(productId, productDetailRequest));
	}

	@PutMapping("/{productId}/update/{id}")
	public ResponseEntity<?> updateProductDetail(@PathVariable("productId") Long productId,
	                                             @PathVariable("id") Long id,
	                                             @Valid @RequestBody ProductDetailRequest productDetailRequest,
	                                             BindingResult bindingResult) {
		boolean exists = productDetailService.isUpdateExistsByProductSizeAndColor(productId,
				productDetailRequest.getSizeId(), productDetailRequest.getColorId(),id);
		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
		}
		if (exists) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Sản phẩm có màu và kích cỡ này đã tồn tại");
		}
		return ResponseEntity.ok(productDetailService.updateProductDetail(productId, id, productDetailRequest));
	}

	@DeleteMapping("/{productId}/delete/{id}")
	public ResponseEntity<?> deleteProductDetail(@PathVariable("productId") Long productId,
	                                             @PathVariable("id") Long id) {
		boolean deleted = productDetailService.deleteProductDetail(productId, id);
		if (!deleted) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Sản phẩm này đã ràng buộc với đối tượng khác không thể xóa");
		}
		return ResponseEntity.ok("Chi tiết sản phẩm đã được xóa thành công");
	}

}
