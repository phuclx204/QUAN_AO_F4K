package org.example.quan_ao_f4k.model.authentication;

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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user")
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @ManyToOne
    @JoinColumn(name = "address_id",nullable = false)
    private Address address;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "number_phone", length = 20)
    private String numberPhone;

    @Column(name = "status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
