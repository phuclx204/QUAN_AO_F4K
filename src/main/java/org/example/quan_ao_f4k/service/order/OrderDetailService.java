package org.example.quan_ao_f4k.service.order;

import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.response.orders.PdfShopOfflineDTO;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public interface OrderDetailService extends CrudService<OrderProductDetailKey, OrderDetailRequest, OrderDetailResponse> {
	void updateQuantity(Long productId, int quantity);
	void updateQuantityPlus(Long productId, int quantity);
	List<OrderDetail> getOrderDetailsByOrderId(Long orderId);

	void addModelOrderDetail(Model model, String code);
	void updateQuantityOrderDetail(OrderDetailRequest request);
	void updateStatusOrder(Long orderId, Integer newStatus);
	void refreshOrder(Long orderId);
}
