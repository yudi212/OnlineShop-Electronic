package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ImageDTO {

    private Long id;
    private String fileName;
    private byte[] image;
}
