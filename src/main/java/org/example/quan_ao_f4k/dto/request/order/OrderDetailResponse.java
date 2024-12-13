package org.example.quan_ao_f4k.dto.request.order;

import lombok.Data;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.model.general.Image;

import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private Long orderId;
    private Long productDetailId;
    private Integer quantity;
    private BigDecimal price;

    private ProductDetailResponse productDetail;
    private OrderResponse order;
    private BigDecimal purchasePrice;
    private BigDecimal discountPrice;
    private Image image;
}
