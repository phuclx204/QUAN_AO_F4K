package org.example.quan_ao_f4k.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.BaseEntity;
import org.example.quan_ao_f4k.model.address.Address;
import org.example.quan_ao_f4k.model.authentication.User;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "to_name", nullable = false)
    private String toName;

    @Column(name = "to_address", nullable = false)
    private String toAddress;

    @Column(name = "to_phone", nullable = false)
    private String toPhone;

    @Column(name = "total_pay", precision = 10, scale = 2)
    private BigDecimal totalPay;

    @ManyToOne
    @JoinColumn(name = "payment_method_type")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_status")
    private Integer paymentStatus;

    @Column(name = "note")
    private String note;

    @Column(name = "tax", precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(name = "code", length = 20, nullable = false,unique = true)
    private String code;

    @Column(name = "order_type", length = 20, nullable = false,unique = true)
    private String orderType;


    @Column(name = "status", nullable = false)
    private Integer status =1;

}
