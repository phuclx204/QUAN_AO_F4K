package org.example.quan_ao_f4k.dto.response.shop;

import lombok.Builder;
import lombok.Data;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.model.product.Size;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopResponse {

    @Data
    @Builder
    public static class ProductListResponse {
        private String id;
        private String idParent;
        private BigDecimal price;
        private Integer quantity;
        private Color color;
        private Size size;
        private Product product;
        private List<Image> listImage;
    }

    @Data
    @Builder public static class ProductDetailDto {
        private String id;
        private BigDecimal price;
        private Integer quantity;
    }

    @Data
    @Builder
    public static class CartDto {
        private ProductDetail productDetail;
        private Image image;
        private int quantity;
        private BigDecimal total;
    }

    @Data
    @Builder
    public static class CartResponse {
        private List<CartDto> listData;
        private BigDecimal subtotal;
    }
}
