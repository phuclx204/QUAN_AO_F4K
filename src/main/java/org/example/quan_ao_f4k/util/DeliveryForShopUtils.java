package org.example.quan_ao_f4k.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DeliveryForShopUtils {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static String ghn_fee_api = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    public static String ghn_province_api = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";
    public static String ghn_district_api = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";
    public static String ghn_ward_api = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";

    public static String ghn_token = "961e356b-9fc7-11ef-9834-7e8875c3faf5";

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", ghn_token);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public static String getFree(int districtId, String wardCode) {
        HttpHeaders headers = getHeaders();
        ObjectNode bodyNode = JacksonEx.INIT_MAPPER.createObjectNode();
        bodyNode.put("service_type_id", 2);
        bodyNode.put("from_district_id", 3440);
        bodyNode.put("to_district_id", districtId);
        bodyNode.put("to_ward_code", wardCode);
        bodyNode.put("weight", 500);

        HttpEntity<String> entity = new HttpEntity<>(bodyNode.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(ghn_fee_api, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            log.error("getFree: Failed to call API");
            return "Failed to call API";
        }
    }

    public static ResponseEntity<String> getProvince() {
        HttpHeaders headers = getHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(ghn_province_api, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            log.error("getProvince: Failed to call API");
            return null;
        }
    }

    public static ResponseEntity<String> getDistrict(int provinceId) {
        HttpHeaders headers = getHeaders();
        ObjectNode bodyNode = JacksonEx.INIT_MAPPER.createObjectNode();
        bodyNode.put("province_iD", provinceId);
        HttpEntity<String> entity = new HttpEntity<>(bodyNode.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(ghn_district_api, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            log.error("getDistrict: Failed to call API");
            return null;
        }
    }

    public static ResponseEntity<String> getWard(int districtId) {
        HttpHeaders headers = getHeaders();
        ObjectNode bodyNode = JacksonEx.INIT_MAPPER.createObjectNode();
        bodyNode.put("district_id", districtId);
        HttpEntity<String> entity = new HttpEntity<>(bodyNode.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(ghn_ward_api, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            log.error("getWard: Failed to call API");
            return null;
        }
    }
}
