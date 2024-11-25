package org.example.quan_ao_f4k.service.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.quan_ao_f4k.controller.shopping_offline.VNPayConfig;
import org.example.quan_ao_f4k.dto.response.orders.PaymentDTO;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.example.quan_ao_f4k.model.order.PaymentMethod;
import org.example.quan_ao_f4k.repository.order.OrderHistoryRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.repository.order.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {
	private final VNPayConfig vnPayConfig;
	private final OrderRepository orderRepository;
	private final OrderHistoryRepository orderHistoryRepository;
	private final PaymentMethodRepository paymentMethodRepository;

	public void updateOrder(Long orderId, BigDecimal total,String note,String code){
		Order order = orderRepository.findById(orderId).get();
		PaymentMethod paymentMethod = paymentMethodRepository.findById(2L).get();

		order.setOrder_type("OFFLINE");
		order.setPaymentStatus(2);
		order.setStatus(3);
		order.setTotalPay(total);
		order.setNote(note);
		order.setCode(code);
		order.setPaymentMethod(paymentMethod);

		orderRepository.save(order);

		OrderHistory ok = new OrderHistory();
		ok.setOrder(order);
		ok.setNote(note);
		ok.setStatus(3);
		ok.setChangeDate(LocalDateTime.now());

		orderHistoryRepository.save(ok);

	}

	public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
		long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
		String bankCode = request.getParameter("bankCode");
		String orderInfor = request.getParameter("orderInfor");
		String orderNote = request.getParameter("orderNote");
		String orderCode = request.getParameter("orderCode");
		Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
		vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
		vnpParamsMap.put("vnp_OrderInfo", orderInfor);
		vnpParamsMap.put("vnp_OrderNote", orderNote);
		vnpParamsMap.put("vnp_TxnRef", orderCode);
		if (bankCode != null && !bankCode.isEmpty()) {
			vnpParamsMap.put("vnp_BankCode", bankCode);
		}
		vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
		//build query url
		String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
		String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
		String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getVnp_SecretKey(), hashData);
		queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
		String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
		return PaymentDTO.VNPayResponse.builder()
				.code("ok")
				.message("success")
				.paymentUrl(paymentUrl).build();
	}
}

