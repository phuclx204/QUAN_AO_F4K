package org.example.quan_ao_f4k.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "admin")
@Controller
public class GuaranteeController {
	@GetMapping("guarantee")
	public String getGuarantee(Model model) {

		return "admin/product/guarantee";
	}
}
