package org.example.quan_ao_f4k.service.order;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.order.OrderMapper;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.product.ProductServiceImpl;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
	private OrderMapper orderMapper;
	private OrderRepository orderRepository;
	private OrderDetailRepository orderDetailRepository;
	private ProductServiceImpl productService;

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


}
