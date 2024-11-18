package org.example.quan_ao_f4k.model.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.quan_ao_f4k.model.product.ProductDetail;

import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetail {
    @EmbeddedId
    private OrderProductDetailKey orderProductDetailKey = new OrderProductDetailKey();

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @MapsId("productDetailId")
    @JoinColumn(name = "id_parent", nullable = false)
    private ProductDetail productDetail;


    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", precision = 65, scale = 2)
    private BigDecimal price;
}
