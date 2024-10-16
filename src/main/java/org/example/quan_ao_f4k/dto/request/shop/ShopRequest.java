package org.example.quan_ao_f4k.dto.request.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ShopRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestSearch implements Serializable {
        private int pageSize = 6;
        private int page = 0;
        private String name;
        private List<String> brand;
        private BigDecimal priceForm;
        private BigDecimal priceTo;
        private List<String> category;
        private List<String> size;
        private List<String> color;
        private String orderBy = "asc";
    }
}
