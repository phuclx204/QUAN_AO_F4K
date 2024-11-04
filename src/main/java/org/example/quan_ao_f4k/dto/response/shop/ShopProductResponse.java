package org.example.quan_ao_f4k.dto.response.shop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.*;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopProductResponse {

    @Data
    @Builder
    public static class ProductDto {
        private Long id;
        private String name;
        private Category category;
        private Brand brand;
        private String slug;
        private String description;
        private Integer status;
        private Image image;
    }

    @Data
    @Builder
    public static class ProductDetailDto {
        private Long id;
        private ProductDto product;
        private Size size;
        private Color color;
        private BigDecimal price;
        private Integer quantity;
        private Integer status;
        List<Image> images;
    }

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
