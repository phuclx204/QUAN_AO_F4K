package org.example.quan_ao_f4k.dto.request.order;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class OrderRequest {
    @Nullable
    private Long userId;
    private String code = "";

    private String order_type;
    private int status =1;

}
