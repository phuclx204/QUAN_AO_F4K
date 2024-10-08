package org.example.quan_ao_f4k.model.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.BaseEntity;

@Entity
@Table(name = "guarantee")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Guarantee extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private Integer status =1;
}
