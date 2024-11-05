package org.example.quan_ao_f4k.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/test")
public class OrderDetailController {
	@GetMapping
	public String orderDetail() {
		return "/admin/orders/orderDetail";
	}
}
