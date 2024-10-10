package org.example.quan_ao_f4k.controller.product;

import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.service.product.CategoryService;
import org.example.quan_ao_f4k.service.product.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "admin/category")
@Controller
public class CategoryController {

	@Autowired
	private CategoryServiceImpl categoryService;

	@Autowired
	private GenericController<CategoryResponse, CategoryRequest> controller;

	@GetMapping
	public String getCategories(Model model) {
		model.addAttribute("listData", List.of(new CategoryResponse(1L, "demo", "banana"), new CategoryResponse(2L, "demo", "banana")));
		return "admin/product/category";
	}

	@SneakyThrows
    @PostMapping
	@ResponseBody
	public ResponseEntity<CategoryResponse> save(@RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.save(request));
	}

	@SneakyThrows
	@DeleteMapping
	@ResponseBody
	public ResponseEntity<Void> delete(@RequestParam("id") Long id) {
		categoryService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@SneakyThrows
	@PutMapping
	@ResponseBody
	public ResponseEntity<CategoryResponse> update(@RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.save(request.getId(), request));
	}
}
