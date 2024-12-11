package org.example.quan_ao_f4k.controller.order;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderHistoryResponse;
import org.example.quan_ao_f4k.mapper.order.OrderDetailMapper;
import org.example.quan_ao_f4k.mapper.order.OrderHistoryMapper;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.OrderHistoryRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.order.OrderDetailService;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/order-detail")
@AllArgsConstructor
@Slf4j
public class OrderDetailController {
	private final OrderDetailRepository orderDetailRepository;
	private final OrderDetailService orderDetailService;
	private OrderServiceImpl orderService;
	private OrderRepository orderRepository;
	private OrderHistoryRepository orderHistoryRepository;

	private OrderHistoryMapper orderHistoryMapper;
	private OrderDetailMapper orderDetailMapper;

	@GetMapping
	public String orderDetail() {
		return "/admin/orders/orderDetail";
	}

	@GetMapping("/{code}")
	public String getOrderById(@PathVariable String code, Model model) {
		orderDetailService.addModelOrderDetail(model, code);
		return "/admin/orders/order-detail";
	}

	@GetMapping("/get-state/{code}")
	public ResponseEntity<List<OrderHistoryResponse>> getStateOrder(@PathVariable String code) {
		Order order = orderRepository.findOrderByOrderCode(code);
		List<OrderHistory> orderHistories = orderHistoryRepository.findByOrderId(order.getId());
		return ResponseEntity.ok(orderHistoryMapper.entityToResponse(orderHistories));
	}

	@GetMapping("/exists-product-detail")
	public ResponseEntity<OrderDetailResponse> getProductDetailInOrder(@RequestParam("orderId") Long orderId, @RequestParam("productDetailId") Long productDetailId) {
		OrderDetail orderDetail = orderDetailRepository.findByProductDetailIdAndOrderId(orderId, productDetailId);
		return ResponseEntity.ok(orderDetailMapper.entityToResponse(orderDetail));
	}

	@GetMapping("/remove-product-detail")
	public ResponseEntity<?> removeProductDetail(@RequestParam("orderId") Long orderId, @RequestParam("productDetailId") Long productDetailId) {
		OrderDetail orderDetail = orderDetailRepository.findByProductDetailIdAndOrderId(orderId, productDetailId);
		if (orderDetail != null) {
			orderDetailRepository.delete(orderDetail);
		}
		return ResponseEntity.ok().build();
	}

	//API CHO ORDER DETAIL
	@PutMapping("/update-quantity")
	public ResponseEntity<?> updateQuantity(@RequestBody OrderDetailRequest request) {
		orderDetailService.updateQuantityOrderDetail(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/update-status/{orderId}")
	public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @RequestParam("status") Integer status, @RequestParam(required = false, name = "note") String note) {
		orderDetailService.updateStatusOrder(orderId, status, note);
		return ResponseEntity.ok().build();
	}

	@GetMapping("refresh-order/{orderId}")
	public ResponseEntity<?> refreshOrder(@PathVariable Long orderId) {
		orderDetailService.refreshOrder(orderId);
		return ResponseEntity.ok().build();
	}

}
