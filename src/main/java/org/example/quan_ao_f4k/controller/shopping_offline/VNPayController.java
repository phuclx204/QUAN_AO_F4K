package org.example.quan_ao_f4k.controller.shopping_offline;


import jakarta.servlet.http.HttpServletRequest;
import org.example.quan_ao_f4k.dto.response.orders.PaymentDTO;
import org.example.quan_ao_f4k.service.order.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/shopping-offline/checkout/vnpay")
public class VNPayController {
	@Autowired
	private VNPayService vnPayService;

	@GetMapping("/payment")
	public ResponseEntity<PaymentDTO.VNPayResponse> submidOrder(HttpServletRequest request,
	                                                            @RequestParam("amount") long amount,
	                                                            @RequestParam(value = "bankCode", required = false) String bankCode) {
		PaymentDTO.VNPayResponse response = vnPayService.createVnPayPayment(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/payment-callback")
	public String payCallbackHandler(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		String status = request.getParameter("vnp_ResponseCode");
		String orderId = request.getParameter("vnp_OrderInfo");
		String totalPay = request.getParameter("vnp_Amount");
		String orderNote = request.getParameter("vnp_OrderNote");
		String orderCode = request.getParameter("vnp_TxnRef");

		Long id = Long.parseLong(orderId);
		BigDecimal total = BigDecimal.valueOf(Long.parseLong(totalPay)/100);


		if ("00".equals(status)) {
			vnPayService.updateOrder(id,total,orderNote,orderCode);
			redirectAttributes.addFlashAttribute("message", "Thanh toán thành công!");
			redirectAttributes.addFlashAttribute("messageType", "success");
			redirectAttributes.addFlashAttribute("statusPay", status);
		} else {
			redirectAttributes.addFlashAttribute("message", "Thanh toán thất bại!");
			redirectAttributes.addFlashAttribute("messageType", "error");
		}
		return "redirect:/admin/shopping-offline/";
	}


}
