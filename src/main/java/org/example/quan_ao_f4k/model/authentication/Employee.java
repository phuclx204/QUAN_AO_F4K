package org.example.quan_ao_f4k.model.authentication;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quan_ao_f4k.model.BaseEntity;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "employee")
public class Employee extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "salary",precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "employment_type")
    private Integer employmentType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "note")
    private String note;
}
