package org.example.quan_ao_f4k.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnPayConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "/vnPay/vnpay-payment-return";
    public static String vnp_TmnCode = "MIQE8RKJ";
    public static String vnp_HashSecret = "J9KKL6F5KEYAQ68X163TD5CWJCEJ1J6S";
    public static String vnp_apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";


    public static String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(vnp_HashSecret,sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getLocalAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }


    @Getter
    public enum TransactionErrorCode {
        SUCCESS("00", "Giao dịch thành công"),
        PENDING("01", "Giao dịch chưa hoàn tất"),
        ERROR("02", "Giao dịch bị lỗi"),
        REVERSED("04", "Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)"),
        PROCESSING_REFUND("05", "VNPAY đang xử lý giao dịch này (GD hoàn tiền)"),
        REFUND_REQUESTED("06", "VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền)"),
        SUSPICIOUS("07", "Giao dịch bị nghi ngờ gian lận"),
        REFUND_REJECTED("09", "GD Hoàn trả bị từ chối");
        private final String code;
        private final String message;

        TransactionErrorCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static String getMessageByCode(String code) {
            for (TransactionErrorCode error : values()) {
                if (error.code.equals(code)) {
                    return error.message;
                }
            }
            return "Mã lỗi không xác định";
        }
    }


    @Getter
    public enum VnpResponseCode {
        SUCCESS("00", "Giao dịch thành công"),
        TIME_OUT("15", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán."),
        SUSPICIOUS_TRANSACTION("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)."),
        NOT_REGISTERED("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng."),
        AUTHENTICATION_FAILED("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần"),
        TIMEOUT("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch."),
        ACCOUNT_LOCKED("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa."),
        WRONG_OTP("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch."),
        TRANSACTION_CANCELLED("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch"),
        INSUFFICIENT_BALANCE("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch."),
        DAILY_LIMIT_EXCEEDED("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày."),
        BANK_MAINTENANCE("75", "Ngân hàng thanh toán đang bảo trì."),
        WRONG_PAYMENT_PASSWORD("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch"),
        OTHER_ERRORS("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");

        private final String code;
        private final String description;

        VnpResponseCode(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public static String getDescriptionByCode(String code) {
            for (VnpResponseCode response : values()) {
                if (response.code.equals(code)) {
                    return response.description;
                }
            }
            return "Mã phản hồi không xác định";
        }
    }
}
