package org.example.quan_ao_f4k.controller.shopping_offline;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.example.quan_ao_f4k.service.order.VNPayUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
public class VNPayConfig {
	@Getter
	public String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
	@Getter
	public String vnp_Returnurl = "http://localhost:8080/admin/shopping-offline/checkout/vnpay/payment-callback";
	@Getter
	public String vnp_TmnCode = "MIQE8RKJ";
	@Getter
	public String vnp_SecretKey = "J9KKL6F5KEYAQ68X163TD5CWJCEJ1J6S";
	@Getter
	private String vnp_Version="2.1.0";
	@Getter
	private String vnp_Command="pay";
	@Getter
	private String orderType="vnpay_banking";

	public Map<String, String> getVNPayConfig() {
		Map<String, String> vnpParamsMap = new HashMap<>();
		vnpParamsMap.put("vnp_Version", this.vnp_Version);
		vnpParamsMap.put("vnp_Command", this.vnp_Command);
		vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
		vnpParamsMap.put("vnp_CurrCode", "VND");
		vnpParamsMap.put("vnp_OrderType", this.orderType);
		vnpParamsMap.put("vnp_Locale", "vn");
		vnpParamsMap.put("vnp_ReturnUrl", this.vnp_Returnurl);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnpCreateDate = formatter.format(calendar.getTime());
		vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
		calendar.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(calendar.getTime());
		vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
		return vnpParamsMap;
	}
}
