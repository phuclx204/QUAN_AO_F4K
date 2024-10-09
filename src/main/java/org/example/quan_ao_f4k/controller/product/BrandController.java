package org.example.quan_ao_f4k.controller.product;

import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.example.quan_ao_f4k.service.product.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/brand")
public class BrandController extends GenericController<BrandRequest, BrandResponse> {
	@Autowired
	private CrudService<Long, BrandRequest, BrandResponse> crudService;

	@Autowired
	private BrandServiceImpl brandService;


	@GetMapping
	public String getAllBrands(@RequestParam(required = false) String search, Model model) {
		ListResponse<BrandResponse> brands = crudService.findAll(1, 20, "id,desc", null, search, false);
		model.addAttribute("brands", brands.getContent());
		return "admin/product/brand";
	}


	@PostMapping("/add")
	public String createBrand(@ModelAttribute BrandRequest brandRequest) {
		crudService.save(brandRequest);
		return "redirect:/admin/brand";
	}

	@PostMapping("/update/{id}")
	public String updateBrand(@PathVariable("id") Long id, @ModelAttribute BrandRequest brandRequest) {
		brandRequest.setId(id);
		crudService.save(id, brandRequest);
		return "redirect:/admin/brand";
	}

	@GetMapping("/edit/{id}")
	public String getBrandForEdit(@PathVariable("id") Long id, Model model) {
		BrandResponse brand = crudService.findById(id);
		model.addAttribute("brand", brand);
		return "admin/product/brand";
	}

	@PostMapping("/update-status/{id}")
	public String updateBrandStatus(@PathVariable("id") Long id,
	                                @RequestParam("status") int status,
	                                @RequestParam("name") String name
	) {
		BrandRequest brandRequest = new BrandRequest();
		brandRequest.setId(id);
		brandRequest.setName(name);
		brandRequest.setStatus(status);
		crudService.save(id, brandRequest);
		return "redirect:/admin/brand";
	}

	@GetMapping("/exportExcel")
	public void exportExcel(HttpServletResponse response) throws Exception {
		// Đặt loại nội dung và tiêu đề cho response
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=DanhSachThuongHieu.xls");

		// Gọi phương thức exportExcel từ service để tạo file
		brandService.exportExcel(response);
	}
	@GetMapping("/exportPdf")
	public void exportPdf(HttpServletResponse response) throws Exception {
		brandService.exportPdf(response);
	}



}
