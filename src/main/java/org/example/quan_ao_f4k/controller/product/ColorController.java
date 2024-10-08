package org.example.quan_ao_f4k.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "admin")
@Controller
public class ColorController {
	@GetMapping("color")
	public String getColor(Model model) {

		return "admin/product/color";
	}
}
