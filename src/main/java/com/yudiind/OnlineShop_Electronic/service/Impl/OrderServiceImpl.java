package com.yudiind.OnlineShop_Electronic.service.Impl;

import com.yudiind.OnlineShop_Electronic.converter.OrderResponseConverter;
import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.OrderResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.PostOrderRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.Cart;
import com.yudiind.OnlineShop_Electronic.model.entity.Order;
import com.yudiind.OnlineShop_Electronic.model.entity.OrderDetail;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.repository.OrderRepository;
import com.yudiind.OnlineShop_Electronic.service.CartService;
import com.yudiind.OnlineShop_Electronic.service.OrderService;
import com.yudiind.OnlineShop_Electronic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;
    private final OrderResponseConverter orderResponseConverter;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserService userService, CartService cartService, OrderResponseConverter orderResponseConverter) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.cartService = cartService;
        this.orderResponseConverter = orderResponseConverter;
    }

    @Override
    public Integer getAllOrdersCount(){
        User user = userService.getAuthenticateUser();
        return orderRepository.countAllByUser(user)
                .orElseThrow(()-> new ResourceNotFoundException("An error occurred whilst fetching orders count"));
    }

    @Override
    public List<OrderResponseDTO> getAllOrders(Integer page, Integer size) {

        User user =  userService.getAuthenticateUser();
        List<Order> orders = orderRepository.findAllByUserOrderByDateDesc(user, PageRequest.of(page, size));
        return orders
                .stream()
                .map(order -> orderResponseConverter.convertToOrderResponseDTO(order))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO postOrder(PostOrderRequest postOrderRequest) {

        User user = userService.getAuthenticateUser();
        Cart cart = user.getCart();

        if (Objects.isNull(cart) || Objects.isNull(cart.getCartItemList())){
            throw new InvalidArgumentException("Cart is not valid");
        }

        if (cart.getCartItemList()
                .stream()
                .anyMatch
                 (cartItem -> cartItem.getProduct().getStock() < cartItem.getAmount())){
                    throw new InvalidArgumentException("Insufficient amount of product");
        }

        Order savedOrder = new Order();
        savedOrder.setUser(user);
        savedOrder.setShipName(postOrderRequest.getShipName());
        savedOrder.setShipAddress(postOrderRequest.getShipAddress());
        savedOrder.setPhone(postOrderRequest.getPhone());
        savedOrder.setCity(postOrderRequest.getCity());

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        savedOrder.setDate(date);

        savedOrder.setOrderDetail(new ArrayList<>());
        cart.getCartItemList().forEach(cartItem -> {
            Integer currentSellCount = cartItem.getProduct().getSellCount() != null ? cartItem.getProduct().getSellCount() : 0;
            cartItem.getProduct().setSellCount(currentSellCount+ cartItem.getAmount());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setAmount(cartItem.getAmount());
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setOrder(savedOrder);

            savedOrder.getOrderDetail().add(orderDetail);
        });
        savedOrder.setTotalPrice(cart.getTotalPrice());

        Order order = orderRepository.save(savedOrder);
        cartService.emptyCart();
        return orderResponseConverter.convertToOrderResponseDTO(order);
    }
}
