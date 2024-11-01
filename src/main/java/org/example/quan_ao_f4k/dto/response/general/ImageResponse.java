package org.example.quan_ao_f4k.dto.response.general;

import lombok.Data;

@Data
public class ImageResponse {
    private Long id;
    private String nameFile;
    private String fileUrl;
    private Long size;
}
