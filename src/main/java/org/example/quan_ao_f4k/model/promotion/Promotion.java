package org.example.quan_ao_f4k.model.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.quan_ao_f4k.model.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promotion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Promotion extends BaseEntity {
    @Column(name = "name", nullable = false) // Tên cột "name", không cho phép null
    private String name;

    @Column(name = "day_start")
    private LocalDate dayStart;

    @Column(name = "day_end")
    private LocalDate dayEnd;

    @Column(name = "type")
    private Integer type;

    @Column(name = "status", nullable = false)
    private Integer status =1;

    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;
}
