package org.example.quan_ao_f4k.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ColorResponse {
    private Long id;
    private String name;
    private String hex;
    private Integer status;

}
