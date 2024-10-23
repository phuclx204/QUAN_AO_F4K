package org.example.quan_ao_f4k.dto.response.shop;

import lombok.Builder;
import lombok.Data;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Product;
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
}
