package org.example.quan_ao_f4k.dto.request.promotion;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PromotionRequest {

    @Data
    @Builder
    public static class RequestSearch {
        private int size = 6;
        private int page = 0;
        private String name;
        private Integer status;
        private Integer effectiveDate;
    }

    @Data
    @Builder
    public static class ProductDto {

        private Long productId;

        @NotNull(message = "Sản phẩm không được để trống")
        private Long product;

        @NotNull(message = "Vui lòng nhập giá trị giảm")
        private BigDecimal discount;

        private Integer discountType;
    }

    @Data
    @Builder
    public static class Request {

        private Long id;

        @NotBlank(message = "Tên chương trình không được để trống")
        private String name;

        @NotNull(message = "Ngày không được để trống")
        @FutureOrPresent(message = "Ngày phải ở trong tương lai hoặc là ngày hiện tại")
        private LocalDate dayStart;

        @NotNull(message = "Ngày không được để trống")
        @FutureOrPresent(message = "Ngày phải ở trong tương lai hoặc là ngày hiện tại")
        private LocalDate dayEnd;

        @NotNull(message = "Trạng thái không được để trống")
        private Integer status;

        @NotEmpty(message = "Danh sách sản phẩm khuyến mãi không được để trống")
        private List<ProductDto> products;

    }

    @Data
    @Builder
    public static class RequestPromotionDetail {
        private int size = 6;
        private int page = 0;

        private Long promotionId;

        @NotNull(message = "Loại giảm không được để trống")
        private Integer type;

        @NotNull(message = "Giá trị giảm không được để trống")
        private BigDecimal discountValue;

        @NotNull(message = "Sản phẩm không được để trống")
        private List<Long> productId;
    }
}
