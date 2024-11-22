package org.example.quan_ao_f4k.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.quan_ao_f4k.model.authentication.User;

@Entity
@Table(name = "shipping_info")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "address_detail", nullable = false, length = 255)
    private String addressDetail;

    @Column(name = "province_id", nullable = false)
    private Long provinceId;

    @Column(name = "province_name", nullable = false, length = 100)
    private String provinceName;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "ward_code", nullable = false, length = 100)
    private String wardCode;

    @Column(name = "ward_name", nullable = false, length = 100)
    private String wardName;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "status", nullable = false)
    private Integer status = 1;
}
