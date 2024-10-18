package org.example.quan_ao_f4k.model.order;

import lombok.*;

import java.io.Serializable;


@Data
public class OrderDetailId implements Serializable {
	private Long order;
	private Long productDetail;
}
