package org.example.quan_ao_f4k.controller.order;

import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
	@Autowired
	private OrderService orderService;

	@GetMapping({"/",""})
	public String order() {
		return "/admin/orders/order";
	}

	@GetMapping("/all")
	public ResponseEntity<ListResponse<OrderResponse>> orderAll(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id,desc") String sort,
			@RequestParam(required = false) String filter,
			@RequestParam(required = false) String search) {
		ListResponse<OrderResponse> response = orderService.findAll(page, size, sort, filter, search, false);
		return ResponseEntity.ok(response);
	}
}
