package org.example.quan_ao_f4k.model.promotion;


import lombok.Data;

import java.io.Serializable;

@Data
public class PromotionProductId implements Serializable {
	private Long promotion;
	private Long product;
}
