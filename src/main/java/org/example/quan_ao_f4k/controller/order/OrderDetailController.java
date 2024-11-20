package org.example.quan_ao_f4k.controller.order;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.response.orders.OrderHistoryResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.mapper.order.OrderDetailMapper;
import org.example.quan_ao_f4k.mapper.order.OrderHistoryMapper;
import org.example.quan_ao_f4k.mapper.product.ProductDetailMapper;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.OrderHistoryRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/order-detail")
@AllArgsConstructor
@Slf4j
public class OrderDetailController {
	private final OrderDetailRepository orderDetailRepository;
	private OrderServiceImpl orderService;
	private OrderRepository orderRepository;
	private ImageRepository imageRepository;
	private OrderHistoryRepository orderHistoryRepository;

	private ProductDetailMapper productDetailMapper;
	private OrderHistoryMapper orderHistoryMapper;
	private OrderDetailMapper orderDetailMapper;

	@GetMapping
	public String orderDetail() {
		return "/admin/orders/orderDetail";
	}

	@GetMapping("/{code}")
	public String getOrderById(@PathVariable String code, Model model) {

		Order order = orderRepository.findOrderByOrderCode(code);

		List<OrderDetail> orderDetails = orderService.findCart(order.getId());
		List<OrderHistory> orderHistories = orderHistoryRepository.findByOrderId(order.getId());
		List<Image> images = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			List<Image> productImages = imageRepository.getImageProductDetail(
					orderDetail.getProductDetail().getId()
					, orderDetail.getProductDetail().getColor().getId()
					, F4KConstants.TableCode.PRODUCT_DETAIL);

			if (!productImages.isEmpty()) {
				orderDetail.setImage(productImages.get(0));
			}
		}
		model.addAttribute("orderDetails", orderDetails);
		model.addAttribute("orderHistory", orderHistories);
		model.addAttribute("order", order);
		model.addAttribute("images", images);
		return "/admin/orders/orderDetail";
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
}
