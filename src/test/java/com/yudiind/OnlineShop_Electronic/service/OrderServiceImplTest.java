package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.converter.OrderResponseConverter;
import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.OrderResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.PostOrderRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.*;
import com.yudiind.OnlineShop_Electronic.repository.OrderRepository;
import com.yudiind.OnlineShop_Electronic.service.Impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserService userService;
    @Mock
    private OrderResponseConverter orderResponseConverter;
    @Mock
    private CartService cartService;

    @Test
    void get_all_orders_count_success() {

        User user = new User();

        List<Order> orders = Stream.generate(Order::new)
                .limit(5)
                .collect(Collectors.toList());

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(orderRepository.countAllByUser(user)).thenReturn(Optional.of(orders.size()));

        Integer result = orderService.getAllOrdersCount();

        assertNotNull(result);
        assertEquals(result, orders.size());
    }

    @Test
    void throw_exception_when_get_all_order_not_found() {

        User user = new User();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(orderRepository.countAllByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> orderService.getAllOrdersCount())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("An error occurred whilst fetching orders count");
    }

    @Test
    void should_get_all_orders() {

        User user = new User();

        Integer page = 2;
        Integer size = 3;

        Order order = new Order();

        List<Order> orders = Stream.generate(()-> order)
                .limit(6)
                .collect(Collectors.toList());

        OrderResponseDTO orderResponse = new OrderResponseDTO();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(orderRepository.findAllByUserOrderByDateDesc(user, PageRequest.of(page, size))).thenReturn(orders);
        when(orderResponseConverter.convertToOrderResponseDTO(order)).thenReturn(orderResponse);

        List<OrderResponseDTO> orderResponseList = orderService.getAllOrders(page, size);

        assertNotNull(orderResponseList);
        assertEquals(orderResponseList.size(), orders.size());

        orderResponseList.forEach(orderResponse1 -> then(orderResponse1).isEqualTo(orderResponse));
    }

    @Test
    void post_order_success() {

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = new Product();
        product.setSellCount(5);
        product.setStock(4);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setAmount(2);

        cart.setCartItemList(Arrays.asList(cartItem));
        cart.setTotalPrice(1000F);

        PostOrderRequest postOrderRequest = new PostOrderRequest();
        postOrderRequest.setShipName("edtri");

        OrderResponseDTO orderExpected = new OrderResponseDTO();

        Order order = new Order();

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(orderRepository.save(orderArgumentCaptor.capture())).thenReturn(order);
        when(orderResponseConverter.convertToOrderResponseDTO(order)).thenReturn(orderExpected);

        OrderResponseDTO result = orderService.postOrder(postOrderRequest);

        verify(orderRepository, times(1)).save(orderArgumentCaptor.getValue());
        verify(orderResponseConverter, times(1)).convertToOrderResponseDTO(order);

        assertNotNull(result);
        assertEquals(orderExpected, result);
    }

    @Test
    void throw_exception_when_post_order_has_null_cart() {

        User user = new User();

        when(userService.getAuthenticateUser()).thenReturn(user);

        assertThatThrownBy(()-> orderService.postOrder(new PostOrderRequest()))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Cart is not valid");
    }

    @Test
    void throw_exception_when_post_order_has_out_of_stock_item() {

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = new Product();
        product.setStock(2);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setAmount(4);

        cart.setCartItemList(Arrays.asList(cartItem));

        when(userService.getAuthenticateUser()).thenReturn(user);

        assertThatThrownBy(()-> orderService.postOrder(new PostOrderRequest()))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Insufficient amount of product");

    }
}
