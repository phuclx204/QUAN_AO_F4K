package org.example.quan_ao_f4k.controller.shopping_offline;

import lombok.AllArgsConstructor;

import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.order.OrderDetailServiceimpl;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/admin/shopping-offline")
@AllArgsConstructor
public class ShoppingController {
	private final OrderServiceImpl orderService;
	private final OrderRepository orderRepository;
	private final OrderDetailServiceimpl orderDetailService;
	private ImageRepository imageRepository;

	@GetMapping({"","/"})
	public String getOrdersWithStatusFive(Model model) {
		try {
			orderService.addModelOrder(model);
		} catch (Exception e) {
			return "error";
		}
		return "/shopping_offline/shopping";
	}
	@PostMapping()
	public ResponseEntity<?> add(@RequestBody OrderRequest request) {
		try {
			OrderResponse orderResponse = orderService.save(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public String getOrderById(@PathVariable Long id, Model model) {
		OrderResponse orderResponse = orderService.findById(id);

		List<OrderDetail> orderDetails = orderService.findCart(id);
		List<Image> images = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			// Lấy hình ảnh của sản phẩm tương ứng với ProductDetail
			List<Image> productImages = imageRepository.getImageByIdParent(orderDetail.getProductDetail().getId(), "PRODUCT_DETAIL");

			// Lưu hình ảnh đầu tiên của sản phẩm vào OrderDetail (nếu có)
			if (!productImages.isEmpty()) {
				orderDetail.setImage(productImages.get(0));  // Giả sử OrderDetail có setter cho image
			}
		}
		BigDecimal totalAmount = orderDetails.stream()
				.map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		DecimalFormat df = new DecimalFormat("#.##");
		orderService.addModelOrder(model);
		System.out.println("Total Amount: " + totalAmount);
		model.addAttribute("order", orderResponse);
		model.addAttribute("orderDetails", orderDetails);
		model.addAttribute("total", df.format(totalAmount));
		model.addAttribute("images", images); // Thêm hình ảnh vào model

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

	@PutMapping("/{orderId}/{productDetailId}")
	public ResponseEntity<OrderDetailResponse> updateOrderDetail(
			@PathVariable Long orderId,
			@PathVariable Long productDetailId,
			@RequestBody OrderDetailRequest request) {

		OrderProductDetailKey key = new OrderProductDetailKey();
		key.setOrderId(orderId);
		key.setProductDetailId(productDetailId);

		OrderDetailResponse response = orderDetailService.save(key, request);

		return ResponseEntity.ok(response);
	}
}