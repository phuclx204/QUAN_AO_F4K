package org.example.quan_ao_f4k.service.shop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.config.VnPayConfig;
import org.example.quan_ao_f4k.dto.response.shop.VnPayStatusResponse;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.HoaDonUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class VnPayService {
    private final F4KUtils f4KUtils;
    private final ShopCheckOutService shopCheckOutService;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductDetailRepository productDetailRepository;

    @Transactional
    public String createOrder(HttpServletRequest request, int amount, String orderInfor, String urlReturn) {
        try {
            Order order = shopCheckOutService.createOneOrder(HoaDonUtils.PhuongThucMuaHang.CHUYEN_TIEN, false);

            // tạo sanbox thanh toán online
            Map<String, String> vnp_Params = initParamVnPay(
                    VnPayConfig.getIpAddress(request)
                    , amount
                    , orderInfor
                    , urlReturn);
            // create order code
            vnp_Params.put("vnp_TxnRef", order.getCode());

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    try {
                        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                        //Build query
                        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                        query.append('=');
                        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String salt = VnPayConfig.vnp_HashSecret;
            String vnp_SecureHash = VnPayConfig.hmacSHA512(salt, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;
            return paymentUrl;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "/shop/checkout";
        }
    }

    public void handleOrderReturn(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = f4KUtils.getUser();
        VnPayStatusResponse vnPayStatusResponse = orderReturn(request);

        // Tìm đơn hàng
        Order order = orderRepository.findByCodeAAndUser_Id(vnPayStatusResponse.getMaHoaDon(), user.getId()).orElse(null);

        // Kiểm tra nếu không tìm thấy đơn hàng
        if (order == null) {
            log.error("Không tìm thấy đơn hàng với mã: " + vnPayStatusResponse.getMaHoaDon() + " và userId: " + user.getId());
            redirectAttributes.addFlashAttribute("errMess", "Có lỗi khi tìm kiếm đơn hàng, xin hãy thử lại");
            return;
        }

        // Cập nhật trạng thái đơn hàng dựa trên kết quả thanh toán
        if (vnPayStatusResponse.isSuccess()) {
            order.setStatus(HoaDonUtils.TrangThaiHoaDon.CHO_XAC_NHAN.getStatus());
            order.setPaymentStatus(HoaDonUtils.TrangThaiThanhToan.DA_THANH_TOAN);
            order.setNote(vnPayStatusResponse.getMessage());

            // Xoá sản phẩm khi thanh toán xong
            updateProductDetail(order);

            // Thanh toán thành công, xóa giỏ hàng
            shopCheckOutService.clearCart(f4KUtils.getUser());
            redirectAttributes.addFlashAttribute("createOrderSuccess", true);
        } else {
            order.setStatus(HoaDonUtils.TrangThaiHoaDon.HUY_DON.getStatus());
            order.setPaymentStatus(HoaDonUtils.TrangThaiThanhToan.CHUA_THANH_TOAN);
            order.setNote(vnPayStatusResponse.getMessage());
            log.error("Thanh toán thất bại. Giữ lại giỏ hàng.");
            redirectAttributes.addFlashAttribute("errMess", "Thanh toán thất bại do: " + vnPayStatusResponse.getMessage());
        }

        // Cập nhật thời gian và lưu đơn hàng
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private void updateProductDetail(Order order) {
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailsByOrderId(order.getId());
        for (OrderDetail orderDetail: orderDetails) {
            Integer quantity = orderDetail.getProductDetail().getQuantity() - orderDetail.getQuantity();
            ProductDetail productDetail = orderDetail.getProductDetail();
            productDetail.setQuantity(quantity);
            productDetailRepository.save(productDetail);
        }
    }

    private VnPayStatusResponse orderReturn(HttpServletRequest request) {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VnPayConfig.hashAllFields(fields);
        VnPayStatusResponse vnPayStatusResponse = new VnPayStatusResponse();
        vnPayStatusResponse.setMaHoaDon(request.getParameter("vnp_TxnRef"));
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                vnPayStatusResponse.setSuccess(true);
            } else {
                vnPayStatusResponse.setSuccess(false);
            }

            String responseCode = request.getParameter("vnp_ResponseCode");
            vnPayStatusResponse.setTransactionCode(responseCode);
            vnPayStatusResponse.setMessage(
                    VnPayConfig.VnpResponseCode.getDescriptionByCode(vnPayStatusResponse.getTransactionCode()));
        } else {
            vnPayStatusResponse.setSuccess(false);
            vnPayStatusResponse.setTransactionCode(VnPayConfig.TransactionErrorCode.ERROR.getCode());
            vnPayStatusResponse.setMessage(VnPayConfig.TransactionErrorCode.getMessageByCode(vnPayStatusResponse.getTransactionCode()));
        }
        return vnPayStatusResponse;
    }

    private Map<String, String> initParamVnPay(String vnp_IpAddr, int amount, String orderInfor, String urlReturn) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
//        String vnp_TxnRef = VnPayConfig.getRandomNumber(8);
//        String vnp_TxnRef = HoaDonUtils.taoMaHoaDon();
//        String vnp_IpAddr = VnPayConfig.getIpAddress(request);
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;
        String orderType = "order-type";
        String bankCode = "NCB";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);

//        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfor);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        urlReturn += VnPayConfig.vnp_ReturnUrl;
        vnp_Params.put("vnp_ReturnUrl", urlReturn);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.SECOND, 30);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        return vnp_Params;
    }
}
