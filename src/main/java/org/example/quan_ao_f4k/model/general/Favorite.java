package org.example.quan_ao_f4k.model.general;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.BaseEntity;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.product.Product;

@Entity
@Table(name = "favorite")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Favorite extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
