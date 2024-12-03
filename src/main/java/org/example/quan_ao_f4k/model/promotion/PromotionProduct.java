package org.example.quan_ao_f4k.model.promotion;

import jakarta.persistence.*;
import lombok.*;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;

import java.math.BigDecimal;

@Entity
@Table(name = "promotion_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@IdClass(PromotionProductId.class)
public class PromotionProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductDetail productDetail;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "status")
    private int status;

    @Column(name = "type")
    private Integer type;
}
