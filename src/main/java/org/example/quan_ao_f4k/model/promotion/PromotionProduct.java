package org.example.quan_ao_f4k.model.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.product.Product;

import java.math.BigDecimal;

@Entity
@Table(name = "promotion_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotionProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount_value",precision = 10,scale = 2)
    private BigDecimal discountValue;

    @Column(name = "status")
    private Integer status;

    @Column(name = "type")
    private Integer type;


}
