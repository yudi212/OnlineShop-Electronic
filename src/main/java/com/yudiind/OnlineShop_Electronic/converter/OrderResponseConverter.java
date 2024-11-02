package com.yudiind.OnlineShop_Electronic.converter;

import com.yudiind.OnlineShop_Electronic.model.dto.CategoryDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.OrderDetailDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.OrderResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.Image;
import com.yudiind.OnlineShop_Electronic.model.entity.Order;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderResponseConverter {

    public OrderResponseDTO convertToOrderResponseDTO(Order order){

        List<OrderDetailDTO> dtoList = order.getOrderDetail()
                .stream()
                .map(detail -> {
                    OrderDetailDTO dto = new OrderDetailDTO();
                    dto.setName(detail.getProduct().getName());
                    dto.setPrice(detail.getProduct().getPrice());
                    dto.setAmount(detail.getAmount());
                    dto.setCategory(CategoryDTO
                            .builder()
                            .name(detail.getProduct().getCategory().getName())
                            .build());

                    List<Image> images = detail.getProduct().getImages();
                    if (images != null && !images.isEmpty()){
                        dto.setImage("/image" + images.get(0).getFileName());
                    } else {
                        dto.setImage(null);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(order.getId());
        response.setShipName(order.getShipName());
        response.setShipAddress(order.getShipAddress());
        response.setCity(order.getCity());
        response.setDate(order.getDate().getTime());
        response.setPhone(order.getPhone());
        response.setOrderItems(dtoList);
        response.setPhone(order.getPhone());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setTotalPrice(order.getTotalPrice());

        return response;

    }

}
