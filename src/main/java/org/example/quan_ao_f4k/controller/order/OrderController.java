package org.example.quan_ao_f4k.controller.order;

import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("${api.prefix}/admin/orders")
public class OrderController {
	@Autowired
	private OrderService orderService;

	@GetMapping({"/",""})
	public String order() {
		return "/admin/orders/order";
	}

	@GetMapping("/all")
	public ResponseEntity<ListResponse<OrderResponse>> searchOrders(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) Integer status) {

		if (startDate != null) {
			startDate = startDate.toLocalDate().atStartOfDay();
		}
		if (endDate != null) {
			endDate = endDate.toLocalDate().atTime(23, 59, 59);
		}

		ListResponse<OrderResponse> response = orderService.searchOrders(page, size, "id,desc", startDate, endDate, search, status);
		return ResponseEntity.ok(response);
	}



}
