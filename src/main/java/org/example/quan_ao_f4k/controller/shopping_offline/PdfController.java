package org.example.quan_ao_f4k.controller.shopping_offline;

import com.itextpdf.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.response.orders.PdfShopOfflineDTO;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.service.order.OrderDetailService;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class PdfController {

    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderServiceImpl orderService;

    @GetMapping("/generate-pdf/shopping-offline/{orderId}")
    @ResponseBody
    public void generatePdf(@PathVariable Long orderId,
                            HttpServletResponse response,
                            Model model) throws Exception {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        model.addAttribute("currentDateTime", currentDateTime);

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);

        List<PdfShopOfflineDTO> formattedOrderDetails = new ArrayList<>();

        int totalQuantity = 0;
        String totalPay = null;
        String toName = null;
        String toPhone = null;
        String orderCode = null;

        for (OrderDetail detail : orderDetails) {
            PdfShopOfflineDTO dto = new PdfShopOfflineDTO();

            BigDecimal effectivePrice = detail.getDiscountPrice() != null ? detail.getDiscountPrice() : detail.getPrice();

            dto.setProductName(detail.getProductDetail().getProduct().getName() + "-"
                    + detail.getProductDetail().getSize().getName() + "-"
                    + detail.getProductDetail().getColor().getName());
            dto.setQuantity(detail.getQuantity());
            dto.setPrice(effectivePrice);

            BigDecimal total = orderService.calculateAmount(detail);
            dto.setTotal(total);

            dto.setPriceFormatted(formatCurrency(effectivePrice));
            dto.setTotalFormatted(formatCurrency(total));
            formattedOrderDetails.add(dto);

            totalQuantity += detail.getQuantity();
            totalPay = formatCurrency(detail.getOrder().getTotalPay());
            toName = detail.getOrder().getToName();
            toPhone = detail.getOrder().getToPhone();
            orderCode = detail.getOrder().getCode();

        }
        model.addAttribute("orderDetails", formattedOrderDetails);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalPay", totalPay);
        model.addAttribute("toName", toName);
        model.addAttribute("toPhone", toPhone);

        System.out.println("orderDetails   "+ formattedOrderDetails);
        System.out.println("totalQuantity   "+ totalQuantity);
        System.out.println("totalPay   "+ totalPay);
        System.out.println("toName   "+ toName);
        System.out.println("toPhone   "+ toPhone);

        // Sử dụng Flying Saucer để chuyển HTML thành PDF
        ITextRenderer renderer = new ITextRenderer();
        String fontPath = new ClassPathResource("fonts/Roboto-Regular.ttf").getPath();
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Tạo dữ liệu cho template Thymeleaf
        Context context = new Context();
        context.setVariables(model.asMap());
        String htmlContent = templateEngine
                .process("shopping_offline/orderPDF", context);

        // Cấu hình header cho response để tải PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"invoice-" + orderCode + ".pdf\"");
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        // Ghi PDF vào OutputStream (browser)
        try (OutputStream os = response.getOutputStream()) {
            renderer.createPDF(os);
        }
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f VND", amount.setScale(0, RoundingMode.HALF_UP).doubleValue());
    }

}