package org.example.quan_ao_f4k.controller.shopping_offline;


import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.DocumentException;
import lombok.AllArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.dto.response.orders.PdfShopOfflineDTO;
import org.example.quan_ao_f4k.service.pomotion.PromotionServiceImpl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.thymeleaf.context.Context;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.address.AddressService;
import org.example.quan_ao_f4k.service.address.AddressServiceImpl;
import org.example.quan_ao_f4k.service.order.OrderDetailServiceimpl;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;

import org.example.quan_ao_f4k.service.product.ProductDetailService;
import org.example.quan_ao_f4k.service.product.ProductDetailServiceImpl;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/shopping-offline")
@AllArgsConstructor
public class ShoppingController {
    private final SpringTemplateEngine templateEngine;
    private final OrderServiceImpl orderService;
    private final OrderRepository orderRepository;
    private final OrderDetailServiceimpl orderDetailService;
    private final ProductDetailServiceImpl productDetailServiceImpl;
    private final ProductDetailService productDetailService;
    private final ImageRepository imageRepository;
    private final AddressServiceImpl addressService;
    private final PromotionServiceImpl promotionServiceImpl;

    @GetMapping({"", "/"})
    public String getOrdersWithStatusFive(Model model) {
        try {
            orderService.addModelOrder(model);
        } catch (Exception e) {
            return "/error/error_404";
        }
        return "/shopping_offline/shopping";
    }

    @PostMapping()
    public ResponseEntity<?> add(@RequestBody OrderRequest request) {
        try {
            OrderResponse orderResponse = orderService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public String getOrderById(@PathVariable Long id, Model model) {
        OrderResponse orderResponse = orderService.findById(id);
        if (orderResponse == null) {
            return "/error/error_404";
        }
        List<OrderDetail> orderDetails = orderService.findCart(id);

        List<Image> images = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            // Lấy hình ảnh của sản phẩm tương ứng với ProductDetail
            Image productImages = imageRepository.findImageByIdParent(orderDetail.getProductDetail().getProduct().getId(), F4KConstants.TableCode.PRODUCT);
            // Lưu hình ảnh đầu tiên của sản phẩm vào OrderDetail (nếu có)
            if (productImages != null) {
                orderDetail.setImage(productImages);
            }
        }

        BigDecimal totalAmount = orderService.calculateTotalAmount(orderDetails);

        DecimalFormat df = new DecimalFormat("#.##");
        orderService.addModelOrder(model);
        model.addAttribute("provinces", addressService.getAllProvinces());
        model.addAttribute("order", orderResponse);
        model.addAttribute("currentOrderId", orderResponse.getId());
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("total", df.format(totalAmount));

        return "/shopping_offline/shopping";
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id, @RequestBody OrderRequest request) {
        OrderResponse orderResponse = orderService.save(id, request);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOrderDetail(@RequestBody OrderDetailRequest request) {
        OrderDetailResponse orderDetailResponse = orderDetailService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailResponse);
    }

    @PutMapping("/{orderId}/{productDetailId}")
    public ResponseEntity<OrderDetailResponse> updateOrderDetail(
            @PathVariable Long orderId,
            @PathVariable Long productDetailId,
            @RequestBody OrderDetailRequest request) {

        OrderProductDetailKey key = new OrderProductDetailKey();
        key.setOrderId(orderId);
        key.setProductDetailId(productDetailId);
        System.out.println("Price " + request.getPrice());

        OrderDetailResponse response = orderDetailService.save(key, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product-detail-list")
    public ResponseEntity<Page<ProductDetailResponse>> getProductDetailList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String nameProduct,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> sizeIds,
            @RequestParam(required = false) List<Long> colorIds,
            @RequestParam(required = false) BigDecimal priceFrom,
            @RequestParam(required = false) BigDecimal priceTo,
            @RequestParam(defaultValue = "asc") String orderBy
    ) {
        return ResponseEntity.ok(productDetailService.searchProductDetail(page, size, nameProduct, brandIds, categoryIds, sizeIds, colorIds, priceFrom, priceTo, orderBy));
    }

    @GetMapping("/{orderId}/order-details")
    public ResponseEntity<List<OrderDetail>> getProductDetailsByOrderId(@PathVariable Long orderId) {
        List<OrderDetail> productDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(productDetails);
    }

    @GetMapping("/generate-pdf/{orderId}")
    @ResponseBody
    public void generatePdf(@PathVariable Long orderId,
                            HttpServletResponse response,
                            Model model) throws Exception {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
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

            dto.setProductName(detail.getProductDetail().getProduct().getName() + "-"
                    + detail.getProductDetail().getSize().getName() + "-"
                    + detail.getProductDetail().getColor().getName());
            dto.setQuantity(detail.getQuantity());
            dto.setPrice(detail.getPrice());

            BigDecimal quantity = new BigDecimal(detail.getQuantity());
            BigDecimal total = quantity.multiply(detail.getPrice());
            dto.setTotal(total);

            dto.setPriceFormatted(formatCurrency(detail.getPrice()));
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

    @GetMapping("/is-on-sale")
    public ResponseEntity<BigDecimal> calculateDiscount(
            @RequestParam Long productDetailId,
            @RequestParam BigDecimal originalPrice) {

        BigDecimal discountedPrice = promotionServiceImpl.isProductDetailOnSale(productDetailId, originalPrice);

        if (discountedPrice == null) {
            return ResponseEntity.ok(originalPrice); // Nếu không có giảm giá, trả về giá gốc
        }

        return ResponseEntity.ok(discountedPrice);
    }

    @GetMapping("/search-product-detail")
    public ResponseEntity<Page<ProductDetailResponse>> searchProductDetail(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String nameProduct,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sizeId,
            @RequestParam(required = false) Long colorId,
            @RequestParam(required = false) BigDecimal priceFrom,
            @RequestParam(required = false) BigDecimal priceTo,
            @RequestParam(defaultValue = "asc") String orderBy
    ) {
        return ResponseEntity.ok(productDetailService.searchProductDetail(page, size, nameProduct, brandId, categoryId, sizeId, colorId, priceFrom, priceTo, orderBy));
    }
}
