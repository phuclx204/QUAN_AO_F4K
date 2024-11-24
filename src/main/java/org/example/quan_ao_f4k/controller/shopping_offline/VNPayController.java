package org.example.quan_ao_f4k.controller.shopping_offline;


import jakarta.servlet.http.HttpServletRequest;
import org.example.quan_ao_f4k.service.order.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/shopping-offline/checkout/vnpay")
public class VNPayController {
	@Autowired
	private VNPayService vnPayService;

	@GetMapping("/submitOrder")
	public String submidOrder(@RequestParam("amount") int orderTotal,
	                          @RequestParam("orderInfo") String orderInfo,
	                          HttpServletRequest request){
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
		return "redirect:" + vnpayUrl;
	}

	@GetMapping("/vnpay-payment")
	public String GetMapping(HttpServletRequest request, Model model){
		int paymentStatus =vnPayService.orderReturn(request);

		String orderInfo = request.getParameter("vnp_OrderInfo");
		String paymentTime = request.getParameter("vnp_PayDate");
		String transactionId = request.getParameter("vnp_TransactionNo");
		String totalPrice = request.getParameter("vnp_Amount");

		model.addAttribute("orderId", orderInfo);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentTime", paymentTime);
		model.addAttribute("transactionId", transactionId);

		// Xử lý các trạng thái thanh toán
		switch (paymentStatus) {
			case 1:
				model.addAttribute("checkoutStatus", "Thanh toán thành công");
				break;
			case 0:
				model.addAttribute("checkoutStatus", "Thanh toán không thành công");
				break;
			case -1:
			default:
				model.addAttribute("checkoutStatus", "Dữ liệu không hợp lệ");
				break;
		}

		return "redirect:/admin/shopping-offline/";
	}
}
