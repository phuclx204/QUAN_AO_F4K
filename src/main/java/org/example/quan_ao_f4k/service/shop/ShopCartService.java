package org.example.quan_ao_f4k.service.shop;

import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.model.order.Cart;
import org.springframework.ui.Model;

import java.util.List;

public interface ShopCartService {
    void addModelCart(Model model);
    ShopProductResponse.CartResponse getListCart(String username);
    String addCart(Long idProductDetail, int quantity);
    void removeCart(Long idProductDetail);
    Cart getCart(Long userId);
    ShopProductResponse.ShippingInfoDto detailShippingInfo(Long idProductDetail);
    String getFree(int districtId, String wardCode);
    String getProvince();
    String getDistrict(int provinceId);
    String getWard(int districtId);
    void addShippingInfo(ShopProductResponse.ShippingInfoDto shippingInfoDto);
    void setDefaultAddress(Long shippingId);

    List<ShopProductResponse.ShippingInfoDto> getShippingInfo();
}
