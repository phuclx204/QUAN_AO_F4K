package org.example.quan_ao_f4k.controller.order;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.OrderHistoryRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("${api.prefix}/admin/order-detail")
@AllArgsConstructor
public class OrderDetailController {
	private OrderServiceImpl orderService;
	private OrderRepository orderRepository;
	private ImageRepository imageRepository;
	private OrderHistoryRepository orderHistoryRepository;
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

			List<Image> productImages = imageRepository.getImageByIdParent(orderDetail.getProductDetail().getId(), "PRODUCT_DETAIL");

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

}
