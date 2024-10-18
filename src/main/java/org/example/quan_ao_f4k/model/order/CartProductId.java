package org.example.quan_ao_f4k.model.order;


import lombok.*;

import java.io.Serializable;

@Data
public class CartProductId implements Serializable {
	private Long cart;
	private Long productDetail;
}
