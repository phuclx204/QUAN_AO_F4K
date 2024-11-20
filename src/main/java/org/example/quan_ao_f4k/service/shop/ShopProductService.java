package org.example.quan_ao_f4k.service.shop;

import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.ShippingInfo;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

public interface ShopProductService {
    void addModelFilter(Model model);
    Page<ShopProductResponse.ProductDetailDto> searchProducts(ShopProductRequest.RequestSearch requestSearch);
    void addModelProductDetail(Model model, String slug, String colorHex, String sizeName);
}