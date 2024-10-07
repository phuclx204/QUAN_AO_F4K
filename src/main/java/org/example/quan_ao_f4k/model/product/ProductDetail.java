package org.example.quan_ao_f4k.model.product;

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

import java.math.BigDecimal;

@Entity
@Table(name = "product_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDetail extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "product_id",nullable = false)
	private Product product;

	@ManyToOne
	@JoinColumn(name = "size_id",nullable = false)
	private Size size;

	@ManyToOne
	@JoinColumn(name = "color_id",nullable = false)
	private Color color;

	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price;

	@ManyToOne
	@JoinColumn(name = "guarantee_id",nullable = true)
	private Guarantee guarantee;

	@Column(name = "quantity",nullable = false)
	private Integer quantity;

	@Column(name = "thumbnail",nullable = true)
	private String thumbnail;

	@Column(name = "status",nullable = false)
	private Integer status;

}
