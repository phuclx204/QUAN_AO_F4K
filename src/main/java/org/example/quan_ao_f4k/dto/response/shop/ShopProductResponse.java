package org.example.quan_ao_f4k.dto.response.shop;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.PaymentMethod;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.model.promotion.Promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        List<Image> images;
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
        private BigDecimal discountValue;
        private Integer status;
        private Promotion promotion;
    }

    /// FOR CART
    @Data
    @Builder
    public static class CartProductDto {
        private ProductDetailDto productDetailDto;
        private int quantity;
        private BigDecimal total;
        private BigDecimal price;
        private Integer status;
    }

    @Data
    @Builder
    public static class CartResponse {
        private List<CartProductDto> items;
        private BigDecimal subtotal;
        private Integer itemCount;
    }

    ///FOR PAYMENT
    @Data
    @Builder
    public static class ShippingInfoDto {
        private Long id;
        private String recipientName;
        private String phoneNumber;
        private String addressDetail;
        private Long provinceId;
        private String provinceName;
        private Long districtId;
        private String districtName;
        private String wardCode;
        private String wardName;
        private Boolean isDefault = false;
    }

    @Data
    @Builder
    public static class UserDto {
        private Long id;
        private String username;
        private String numberPhone;
        private String email;
        private Integer gender;
        private String addressDetail;
        private String fullName;
    }

    @Data
    @Builder
    public static class OrderDto {
        private Long id;
        private User user;
        private String toName;
        private String toAddress;
        private String toPhone;
        private BigDecimal totalPay;
        private BigDecimal shippingPay;
        private BigDecimal totalCart;
        private BigDecimal invoiceTotal;
        private PaymentMethod paymentMethod;
        private Integer paymentStatus;
        private String note;
        private BigDecimal tax;
        private String code;
        private String order_type;
        private Integer status;
        private String statusText;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    public static class OrderDetailDto {
        private OrderDto orderDto;
        private ProductDetailDto productDetailDto;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private BigDecimal purchasePrice;
    }

    @Data
    @Builder
    public static class OrderResponse {
        private OrderDto orderDto;
        private List<OrderDetailDto> detailDtoList;
    }
}
