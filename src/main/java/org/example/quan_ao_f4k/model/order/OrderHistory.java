package org.example.quan_ao_f4k.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "history_order")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderHistory extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "status")
    private Integer status;

    @Column(name = "change_date")
    private LocalDateTime changeDate;

    @Column(name = "note")
    private String note;

    @Column(name = "create_by")
    private String createBy;

}
