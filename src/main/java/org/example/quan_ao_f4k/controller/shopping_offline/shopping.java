package org.example.quan_ao_f4k.controller.shopping_offline;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.service.product.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/shopping-offline")
@AllArgsConstructor
public class shopping {

	@GetMapping
	public String getAllBrandsPage() {
		return "/shopping_offline/shopping";
	}


}
