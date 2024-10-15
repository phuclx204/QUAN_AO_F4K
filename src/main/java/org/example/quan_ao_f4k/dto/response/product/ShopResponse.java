package org.example.quan_ao_f4k.dto.response.product;

import lombok.*;
import org.apache.poi.hpsf.Decimal;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.ProductDetail;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShopResponse {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductResponse implements Serializable {
        private Long id;
        private String name;
        private Decimal price;
        private List<Image> imageList;
    }
}


