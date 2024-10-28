package org.example.quan_ao_f4k.controller.shopping_offline;

import lombok.AllArgsConstructor;

import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.order.OrderDetailServiceimpl;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/shopping-offline")
@AllArgsConstructor
public class ShoppingController {
	private final OrderServiceImpl orderService;
	private final OrderRepository orderRepository;
	private final OrderDetailServiceimpl orderDetailService;

	@GetMapping("")
	public String getOrdersWithStatusFive(Model model) {
		try {
			orderService.addModelOrder(model);
		} catch (Exception e) {
			return "error";
		}
		return "/shopping_offline/shopping";
	}
	@PostMapping("")
	public ResponseEntity<OrderResponse> add(@RequestBody OrderRequest request) {
			OrderResponse orderResponse = orderService.save(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);

	}
	@GetMapping("/{id}")
	public String getOrderById(@PathVariable Long id,Model model) {
			OrderResponse orderResponse= orderService.findById(id);

			orderService.addModelOrder(model);
			model.addAttribute("order",orderResponse);
			model.addAttribute("orderDetails",orderService.findCart(id));

			return "/shopping_offline/shopping";
	}
	@PutMapping("/{id}")
	public ResponseEntity<OrderResponse> update(@PathVariable Long id, @RequestBody OrderRequest request) {
		OrderResponse orderResponse = orderService.save(id, request);
		return ResponseEntity.ok(orderResponse);
	}

	@PostMapping("/add")
	public ResponseEntity<?> addOrderDetail(@RequestBody OrderDetailRequest request) {
		OrderDetailResponse orderDetailResponse = orderDetailService.save(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailResponse);
	}

}
