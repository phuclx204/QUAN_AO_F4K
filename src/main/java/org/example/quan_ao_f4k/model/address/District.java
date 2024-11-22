package org.example.quan_ao_f4k.model.address;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "district")
public class District extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private int code;

    @ManyToOne()
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;
}
