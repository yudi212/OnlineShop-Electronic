package com.yudiind.OnlineShop_Electronic.controller;

import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.model.dto.OrderResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.PostOrderRequest;
import com.yudiind.OnlineShop_Electronic.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAllOrderCount(){
        Integer orderCount = orderService.getAllOrdersCount();
        return new ResponseEntity<>(orderCount, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrder(@RequestParam("page") Integer page,
                                                              @RequestParam("size") Integer size){

        if (Objects.isNull(page) || page < 0){
            throw new InvalidArgumentException("Invalid page");
        }
        if (Objects.isNull(size) || size < 0){
            throw new InvalidArgumentException("Invalid pageSize");
        }

        List<OrderResponseDTO> response = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> postOrder(@RequestBody @Valid PostOrderRequest postOrderRequest){
        OrderResponseDTO orderResponse = orderService.postOrder(postOrderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

}
