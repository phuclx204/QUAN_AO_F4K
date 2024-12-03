package org.example.quan_ao_f4k.service.order;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.dto.response.orders.OrderStatisticsResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.order.OrderMapper;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.repository.address.DistrictRepository;
import org.example.quan_ao_f4k.repository.address.ProvinceRepository;
import org.example.quan_ao_f4k.repository.address.WardRepository;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.product.ProductServiceImpl;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
	private OrderMapper orderMapper;
	private OrderRepository orderRepository;
	private OrderDetailRepository orderDetailRepository;
	private ProductServiceImpl productService;
	private WardRepository wardRepository;
	private DistrictRepository districtRepository;
	private ProvinceRepository provinceRepository;

	@Override
	public ListResponse<OrderResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page, size, sort, filter, search, all, SearchFields.ORDER, orderRepository, orderMapper);
	}

	@Override
	public OrderResponse findById(Long aLong) {
		Order order = orderRepository.findById(aLong).get();
		return orderMapper.entityToResponse(order);
	}

	@Override
	public OrderResponse save(OrderRequest request) {
		return defaultSave(request, orderRepository, orderMapper);
	}

	@Override
	public OrderResponse save(Long aLong, OrderRequest request) {
		return defaultSave(aLong, request, orderRepository, orderMapper, "");
	}

	@Override
	public void delete(Long aLong) {
		orderRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		orderRepository.deleteAllById(longs);
	}

	public List<OrderResponse> findOrdersByOrderType(String orderType, int status) {
		return orderRepository.findOrdersByStatus(orderType, status)
				.stream()
				.map(orderMapper::entityToResponse)
				.collect(Collectors.toList());
	}

	public List<OrderDetail> findCart(Long idOrder) {
		return orderDetailRepository.findOrderDetailsByOrderId(idOrder);
	}

	public void addModelOrder(Model model) {
		model.addAttribute("listOrder", this.findOrdersByOrderType("OFFLINE", 1));
		model.addAttribute("wards", wardRepository.findAll());
		model.addAttribute("districts", districtRepository.findAll());
		model.addAttribute("provinces", provinceRepository.findAll());
	}

	// Tính số tiền cho từng chi tiết đơn hàng
	private BigDecimal calculateAmount(OrderDetail detail) {
		BigDecimal price = detail.getPrice();
		BigDecimal quantity = BigDecimal.valueOf(detail.getQuantity());
		return (price != null ? price.multiply(quantity) : BigDecimal.ZERO);
	}

	// Tính tổng số tiền từ danh sách chi tiết đơn hàng
	public BigDecimal calculateTotalAmount(List<OrderDetail> orderDetails) {
		return orderDetails.stream()
				.map(this::calculateAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public ListResponse<OrderResponse> searchOrders(int page, int size, String sort, LocalDateTime startDate, LocalDateTime endDate,
	                                                String search, Integer status) {
		Pageable pageable = PageRequest.of(page - 1, size);

		// Áp dụng phân trang và lọc
		Page<Order> orders = orderRepository.searchOrders(pageable, startDate, endDate, search, status);

		// Chuyển đổi danh sách Order thành OrderResponse
		List<OrderResponse> orderResponses = orders.getContent().stream()
				.map(orderMapper::entityToResponse)
				.collect(Collectors.toList());

		return new ListResponse<>(orderResponses, orders);
	}

	@Override
	public Map<LocalDate, OrderStatisticsResponse> getOrderStatistics(LocalDate startDate, LocalDate endDate) {
		// Lấy tất cả các đơn hàng từ kho dữ liệu
		List<Order> allOrders = orderRepository.findAll();

		// Lọc các đơn hàng theo ngày và trạng thái hóa đơn (chỉ lấy những đơn hàng có trạng thái là 3 và nằm trong khoảng thời gian startDate và endDate)
		List<Order> filteredOrders = allOrders.stream()
				.filter(order -> {
					LocalDate createdDate = order.getCreatedAt().toLocalDate();
					// Kiểm tra ngày tạo và trạng thái của đơn hàng
					return !createdDate.isBefore(startDate) && !createdDate.isAfter(endDate) && order.getStatus() == 3;
				})
				.collect(Collectors.toList());

		// Nhóm các đơn hàng theo ngày và tính toán thống kê cho mỗi ngày
		return filteredOrders.stream()
				.collect(Collectors.groupingBy(
						order -> order.getCreatedAt().toLocalDate(), // Nhóm đơn hàng theo ngày tạo
						Collectors.collectingAndThen(
								Collectors.toList(), // Thu thập các đơn hàng vào một danh sách
								ordersByDate -> {
									// Tính toán số lượng đơn hàng trong ngày
									long count = ordersByDate.size();
									// Tính tổng số tiền thanh toán cho các đơn hàng trong ngày
									BigDecimal totalPay = ordersByDate.stream()
											.map(Order::getTotalPay)
											.reduce(BigDecimal.ZERO, BigDecimal::add);
									// Trả về một đối tượng OrderStatisticsResponse với các thống kê
									return new OrderStatisticsResponse(count, totalPay, ordersByDate.get(0).getCreatedAt().toLocalDate());
								}
						)
				));
	}

	@Override
	public BigDecimal getTotalRevenue() {
		return orderRepository.getTotalPay();
	}

	@Override
	public Integer getTotalQuantityOrders() {
		return orderRepository.getTotalQuantityOrders();
	}

	@Override
	public Integer getTotalProductQuantityInCompletedOrders() {
		return orderRepository.getTotalProductQuantityInCompletedOrders();
	}

	@Override
	public 	Map<String, BigDecimal> getTotalPayByOrderType(){
		List<Object[]> results = orderRepository.getTotalPayByOrderType();
		return results.stream().collect(Collectors.toMap(
				result -> (String) result[0],  // orderType
				result -> (BigDecimal) result[1] // totalPay
		));
	}

}
