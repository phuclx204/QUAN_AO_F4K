package org.example.quan_ao_f4k.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
public class OrderProductDetailKey {
    @Column(name = "order_id", nullable = false)
    Long orderId;

    @Column(name = "id_parent", nullable = false)
    Long productDetailId;
}
