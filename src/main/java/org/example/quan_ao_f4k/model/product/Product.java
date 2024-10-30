package org.example.quan_ao_f4k.model.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.quan_ao_f4k.model.BaseEntity;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Product extends BaseEntity {

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "category_id",nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "brand_id",nullable = false)
	private Brand brand;

	@Column(name = "thumbnail")
	private String thumbnail;

	@Column(name = "description")
	private String description;

	@Column(name = "status", nullable = false)
	private Integer status =1;
}
