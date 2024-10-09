package org.example.quan_ao_f4k.controller.product;

import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.service.product.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping(value = "admin")
@Controller
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private GenericController<CategoryResponse, CategoryRequest> controller;

	@GetMapping("category")
	public String getCategories(Model model) {
		model.addAttribute("listData", List.of(new CategoryResponse(1L, "demo", "banana"), new CategoryResponse(2L, "demo", "banana")));
		return "admin/product/category";
	}

	@SneakyThrows
    @PostMapping("category")
	@ResponseBody
	public ResponseEntity save(Model model) {
		throw new BadRequestException("Sai lòi le");
	}

	@GetMapping("category/list")
	@ResponseBody
	public List<CategoryResponse> getDemo(Model model) {
		return List.of(new CategoryResponse(1L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
				,new CategoryResponse(2L, "demo", "banana")
		);
	}
}
