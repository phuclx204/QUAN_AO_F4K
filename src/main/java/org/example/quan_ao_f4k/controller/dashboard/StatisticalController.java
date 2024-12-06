package org.example.quan_ao_f4k.controller.dashboard;

import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.dto.response.orders.OrderStatisticsResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailDTO;
import org.example.quan_ao_f4k.service.order.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/statistical")
@RequiredArgsConstructor
public class StatisticalController {
	private final OrderService orderService;

	@GetMapping({"/",""})
	public String getStatistical() {
		return "/admin/dashboard/statistical";
	}

	@GetMapping("/data")
	public ResponseEntity<Map<LocalDate, OrderStatisticsResponse>> getOrderStatistics(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		Map<LocalDate, OrderStatisticsResponse> statistics = orderService.getOrderStatistics(startDate, endDate);
		return ResponseEntity.ok(statistics);
	}
	@GetMapping("/total-revenue")
	public ResponseEntity<BigDecimal> getTotal() {
		BigDecimal totalRevenue = orderService.getTotalRevenue();
		return ResponseEntity.ok(totalRevenue);
	}
	@GetMapping("/total-quantity-order")
	public ResponseEntity<Integer> getTotalQuantityOrder() {
		Integer totalRevenue = orderService.getTotalQuantityOrders();
		return ResponseEntity.ok(totalRevenue);
	}
	@GetMapping("/total-quantity-product")
	public ResponseEntity<Integer> getTotalQuantityProduct() {
		Integer totalRevenue = orderService.getTotalProductQuantityInCompletedOrders();
		return ResponseEntity.ok(totalRevenue);
	}
	@GetMapping("/total-by-order-type")
	public ResponseEntity<Map<String, BigDecimal>> getTotalByOrderType() {
		Map<String, BigDecimal> totalByOrderType = orderService.getTotalPayByOrderType();
		return ResponseEntity.ok(totalByOrderType);
	}

	@GetMapping("/quantity-best-sale")
	public ResponseEntity<List<ProductDetailDTO>> getBestSellingProducts(
			@RequestParam(value = "filterType", required = false) String filterType,
			@RequestParam(value = "filterValue", required = false) String filterValue,
			@RequestParam(value = "orderType", required = false) String orderType) {

		List<ProductDetailDTO> bestSellingProducts = orderService.findQuantityProductDetailsByFilter(filterType, filterValue, orderType);

		if (bestSellingProducts.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(bestSellingProducts);
	}
}
