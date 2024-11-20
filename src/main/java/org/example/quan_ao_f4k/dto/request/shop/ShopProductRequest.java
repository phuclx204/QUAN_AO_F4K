package org.example.quan_ao_f4k.dto.request.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopProductRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestSearch implements Serializable {
        private int pageSize = 12;
        private int page = 0;
        private String name;
        private List<Long> brand;
        private BigDecimal priceForm;
        private BigDecimal priceTo;
        private List<Long> category;
        private List<Long> size;
        private List<Long> color;
        private String orderBy = "asc";
    }
}
