package org.example.quan_ao_f4k.controller.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.product.ProductDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping(value = "/admin/products/product-detail")
@Controller
@AllArgsConstructor
public class ProductDetailController {

	private final ProductDetailService productDetailService;


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
	public ResponseEntity<ProductDetailResponse> addProductDetail(@PathVariable("productId") Long productId,
	                                                              @RequestBody ProductDetailRequest productDetailRequest) {
		return ResponseEntity.ok(productDetailService.addProductDetail(productId, productDetailRequest));
	}

	@PutMapping("/{productId}/update/{id}")
	public ResponseEntity<ProductDetailResponse> updateProductDetail(@PathVariable("productId") Long productId,
	                                                                 @PathVariable("id") Long id,
	                                                                 @RequestBody ProductDetailRequest productDetailRequest) {
		return ResponseEntity.ok(productDetailService.updateProductDetail(productId,id, productDetailRequest));
	}

}
