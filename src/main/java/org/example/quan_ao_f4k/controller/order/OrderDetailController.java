package org.example.quan_ao_f4k.controller.order;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
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
@RequestMapping("/admin/order-detail")
@AllArgsConstructor
public class OrderDetailController {
	private OrderServiceImpl orderService;
	private OrderRepository orderRepository;
	private ImageRepository imageRepository;
	@GetMapping
	public String orderDetail() {
		return "/admin/orders/orderDetail";
	}

	@GetMapping("/{code}")
	public String getOrderById(@PathVariable String code, Model model) {

		Order order = orderRepository.findOrderByOrderCode(code);

		List<OrderDetail> orderDetails = orderService.findCart(order.getId());
		List<Image> images = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			// Lấy hình ảnh của sản phẩm tương ứng với ProductDetail
			List<Image> productImages = imageRepository.getImageByIdParent(orderDetail.getProductDetail().getId(), "PRODUCT_DETAIL");

			// Lưu hình ảnh đầu tiên của sản phẩm vào OrderDetail (nếu có)
			if (!productImages.isEmpty()) {
				orderDetail.setImage(productImages.get(0));  // Giả sử OrderDetail có setter cho image
			}
		}
		model.addAttribute("orderDetails", orderDetails);
		model.addAttribute("order", order);
		model.addAttribute("images", images); // Thêm hình ảnh vào model

		return "/admin/orders/orderDetail";
	}

}
