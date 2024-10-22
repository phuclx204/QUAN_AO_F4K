package org.example.quan_ao_f4k.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.BaseEntity;

@Entity
@Table(name = "color")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Color extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "hex")
    private String hex;

    @Column(name = "status", nullable = false)
    private Integer status=1;
}
