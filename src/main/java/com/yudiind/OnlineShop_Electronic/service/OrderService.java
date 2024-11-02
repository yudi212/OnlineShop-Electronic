package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.OrderResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.PostOrderRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderService {

    Integer getAllOrdersCount();

    List<OrderResponseDTO> getAllOrders(Integer page, Integer size);

    OrderResponseDTO postOrder(PostOrderRequest postOrderRequest);
}


