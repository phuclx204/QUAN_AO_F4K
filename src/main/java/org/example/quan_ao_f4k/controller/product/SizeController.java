package org.example.quan_ao_f4k.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "admin")
@Controller
public class SizeController {
	@GetMapping("size")
	public String getSize(Model model) {

		return "admin/product/size";
	}
}
