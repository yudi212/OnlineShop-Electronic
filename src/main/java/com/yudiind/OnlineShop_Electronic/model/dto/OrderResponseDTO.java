package com.yudiind.OnlineShop_Electronic.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;

    private String shipName;

    private String shipAddress;

    private String city;

    private String phone;

    private Float totalPrice;

    private Long date;

    private String trackingNumber;

    private List<OrderDetailDTO> orderItems;

}
