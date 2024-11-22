package org.example.quan_ao_f4k.controller;

import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/admin/")
public class DashBoardController {
	@GetMapping({"/",""})
	public String getDashboardPage() {
		return "/admin/index";
	}

	@GetMapping({"brand/","brand"})
	public String getAllBrandsPage() {
		return "/admin/product/brand";
	}

	@GetMapping({"category/","category"})
	public String getAllCategoryPage() {
		return "/admin/product/category";
	}

	@GetMapping({"color/","color"})
	public String getAllColorPage() {
		return "/admin/product/color";
	}

	@GetMapping({"size/","size"})
	public String getAllColorsPage() {
		return "/admin/product/size";
	}

	@GetMapping({"products/","products"})
	public String getAllProduct() {
		return "/admin/product/products";
	}

	@GetMapping({"orders/","orders"})
	public String order() {
		return "/admin/orders/order";
	}



}
