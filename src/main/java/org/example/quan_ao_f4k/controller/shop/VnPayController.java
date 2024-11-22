package org.example.quan_ao_f4k.controller.shop;

import jakarta.servlet.http.HttpServletRequest;
import org.example.quan_ao_f4k.dto.response.shop.VnPayStatusResponse;
import org.example.quan_ao_f4k.service.shop.ShopProductService;
import org.example.quan_ao_f4k.service.shop.VnPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("vnPay")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @GetMapping({"", "/create"})
    public String home() {
        return "createOrder";
    }

    @GetMapping("/submit-order")
    public String submidOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        vnPayService.handleOrderReturn(request, redirectAttributes);
        return "redirect:/shop/purchase-history";
    }
}
