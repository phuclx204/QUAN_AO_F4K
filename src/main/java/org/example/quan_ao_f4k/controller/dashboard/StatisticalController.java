package org.example.quan_ao_f4k.controller.dashboard;

import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.dto.response.orders.OrderStatisticsResponse;
import org.example.quan_ao_f4k.service.order.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
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
	@GetMapping("/totalRevenue")
	public ResponseEntity<BigDecimal> getTotal() {
		BigDecimal totalRevenue = orderService.getTotalRevenue();
		return ResponseEntity.ok(totalRevenue);
	}
}
