package org.example.quan_ao_f4k.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@IdClass(CartProductId.class)
public class CartProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Id
    @ManyToOne
    @JoinColumn(name = "is_parent")
    private ProductDetail productDetail;


    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
