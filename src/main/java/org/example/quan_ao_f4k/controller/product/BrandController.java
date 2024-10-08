package org.example.quan_ao_f4k.controller.product;

import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.example.quan_ao_f4k.service.product.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/brand")
public class BrandController extends GenericController<BrandRequest, BrandResponse> {
	@Autowired
	private CrudService<Long,BrandRequest,BrandResponse> crudService;

	@Autowired
	private BrandServiceImpl brandService;

	@GetMapping
	public String getAllBrands(Model model) {
		ListResponse<BrandResponse> brands = crudService.findAll(1, 20, "id,desc", null, null, false);
		model.addAttribute("brands", brands.getContent());
		return "admin/product/brand";
	}

	@PostMapping("/add")
	public String createBrand(@ModelAttribute BrandRequest brandRequest) {
		crudService.save(brandRequest);
		return "redirect:/admin/brand";
	}

	@PostMapping("/update")
	public String updateBrand(@ModelAttribute BrandRequest brandRequest) {
		BrandResponse existingBrand = brandService.findByName(brandRequest.getName());

		if (existingBrand != null && !existingBrand.getId().equals(brandRequest.getId())) {
			return "redirect:/admin/brand?error=nameExists";  // Chuyển hướng với lỗi nếu tên đã tồn tại
		}

		crudService.save(brandRequest);
		return "redirect:/admin/brand";
	}

	@GetMapping("/edit/{id}")
	public String getBrandForEdit(@PathVariable("id") Long id, Model model) {
		BrandResponse brand = crudService.findById(id);
		model.addAttribute("brand", brand);
		return "admin/product/brand";
	}
}
