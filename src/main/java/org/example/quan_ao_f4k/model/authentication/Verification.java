package org.example.quan_ao_f4k.model.authentication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.quan_ao_f4k.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Verification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @Column(name = "type", length = 255)
    private String type;
}
