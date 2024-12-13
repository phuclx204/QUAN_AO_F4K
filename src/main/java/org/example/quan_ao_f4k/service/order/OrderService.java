package org.example.quan_ao_f4k.service.order;

import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.dto.response.orders.OrderStatisticsResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailDTO;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface OrderService extends CrudService<Long, OrderRequest, OrderResponse> {
	ListResponse<OrderResponse> searchOrders(int page, int size, String sort, LocalDateTime startDate, LocalDateTime endDate,
	                                         String search, Integer status);

	Map<LocalDate, OrderStatisticsResponse> getOrderStatistics(LocalDate startDate, LocalDate endDate);
	BigDecimal getTotalRevenue();
	Integer getTotalQuantityOrders();
	Integer getTotalProductQuantityInCompletedOrders();
	Map<String, BigDecimal> getTotalPayByOrderType();

	ListResponse<OrderResponse> searchList(int page, int size, String sort, LocalDateTime startDate, LocalDateTime endDate,
											 String search, Integer status, String orderType);
	List<ProductDetailDTO> findQuantityProductDetailsByFilter(String filterType, String filterValue, String orderType);

    OrderResponse findOrderOfflineById(Long aLong);
}