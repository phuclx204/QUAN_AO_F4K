package org.example.quan_ao_f4k.model.authentication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.quan_ao_f4k.model.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "role")
public class Role extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Integer status=1;
}
