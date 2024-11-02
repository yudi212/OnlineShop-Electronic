package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.converter.CartResponseConverter;
import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.CartItemDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.CartResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ConfirmCartRequest;
import com.yudiind.OnlineShop_Electronic.model.entity.Cart;
import com.yudiind.OnlineShop_Electronic.model.entity.CartItem;
import com.yudiind.OnlineShop_Electronic.model.entity.Product;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import com.yudiind.OnlineShop_Electronic.repository.CartRepository;
import com.yudiind.OnlineShop_Electronic.repository.ProductRepository;
import com.yudiind.OnlineShop_Electronic.service.Impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartResponseConverter cartResponseConverter;
    @InjectMocks
    private CartServiceImpl cartService;


    @Test
    void add_to_cart_when_cart_is_empty() {

        // given
        Long productId = 1L;
        Integer amount = 2;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(100F);
        product.setStock(amount + 1);

        CartResponseDTO cartResponseExpected = new CartResponseDTO();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartResponseConverter.converToCartResponseDTO(cart)).thenReturn(cartResponseExpected);

        // when
        CartResponseDTO cartResponseResult = cartService.addToCart(productId, amount);

        assertNotNull(cartResponseResult);
        assertEquals(cartResponseResult, cartResponseExpected);
    }


    @Test
    void it_should_throw_exception_when_stock_is_insufficient() {
        // given
        Long productId = 1L;
        Integer amount = 3;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(100F);
        product.setStock(amount - 1);

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () -> {
            cartService.addToCart(productId, amount);
        });

        // then
        assertEquals(exception.getMessage(), "Product does not have desired stock ");
    }


    @Test
    void it_should_add_to_cart_when_cart_is_null() {
        // given
        Long productId = 1L;
        Integer amount = 1;

        User user = new User();
        user.setCart(null);

        Cart cart = new Cart();

        Product product = new Product();
        product.setId(productId);
        product.setPrice(100F);
        product.setStock(amount + 1);

        CartResponseDTO cartResponseExpected = new CartResponseDTO();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartResponseConverter.converToCartResponseDTO(any(Cart.class))).thenReturn(cartResponseExpected);

        // when
        CartResponseDTO cartResponseResult = cartService.addToCart(productId, amount);

        // then
        assertEquals(cartResponseResult, cartResponseExpected);
    }


    @Test
    void it_should_update_cart_when_product_already_exists_in_cart() {
        // given
        Long productId = 1L;
        Integer amount = 2;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(100F);
        product.setStock(amount + 1);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setAmount(1);

        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);

        cart.setCartItemList(cartItemList);

        CartResponseDTO cartResponseExpected = new CartResponseDTO();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartResponseConverter.converToCartResponseDTO(cart)).thenReturn(cartResponseExpected);

        // when
        CartResponseDTO cartResponseResult = cartService.addToCart(productId, amount);

        assertEquals(cartResponseResult, cartResponseExpected);
        assertEquals(cartItem.getAmount(), 3);

    }

    @Test
    void it_should_throw_exception_when_cart_already_have_product_and_no_stock() {

        Long productId = 1L;
        Integer amount = 3;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(100F);
        product.setStock(amount - 1);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setAmount(1);

        List<CartItem> cartItemList =new ArrayList<>();
        cartItemList.add(cartItem);

        cart.setCartItemList(cartItemList);

        when(userService.getAuthenticateUser()).thenReturn(user);

        // whern, then
        assertThatThrownBy(() -> cartService.addToCart(productId, amount))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("Product does not have desire stock");
    }

    @Test
    void it_should_remove_cart_item() {

        Long cartItemId = 1L;
        Integer amount = 2;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        Product product = new Product();
        product.setPrice(100F);

        List<CartItem> cartItemList = new ArrayList<>();

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setProduct(product);
        cartItem.setAmount(4);

        cartItemList.add(cartItem);
        cart.setCartItemList(cartItemList);

        CartResponseDTO cartResponseExpected = new CartResponseDTO();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartResponseConverter.converToCartResponseDTO(cart)).thenReturn(cartResponseExpected);

        CartResponseDTO cartResponseResult = cartService.removeFromCart(cartItemId, amount);

        assertNotNull(cartResponseResult);
        assertEquals(cartResponseExpected, cartResponseResult);
    }

    @Test
    void it_should_remove_cart_item_and_empty_cart() {

        Long cartItemId = 1L;
        Integer amount = 4;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        Product product = new Product();
        product.setPrice(100F);

        List<CartItem> cartItemList = new ArrayList<>();

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setProduct(product);
        cartItem.setAmount(4);

        cartItemList.add(cartItem);
        cart.setCartItemList(cartItemList);

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(userService.saveUser(user)).thenReturn(user);

        CartResponseDTO cartResponseDTOResult = cartService.removeFromCart(cartItemId, amount);

        assertNull(cartResponseDTOResult);
        assertEquals(null, cartResponseDTOResult);
    }

    @Test
    void it_should_decrement_cart_item_and_remove_cart_item() {

        Long cartItemId = 1L;
        Integer amount = 4;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        Product product = new Product();
        product.setPrice(100F);

        List<CartItem> cartItemList = new ArrayList<>();

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setProduct(product);
        cartItem.setAmount(4);
        cartItemList.add(cartItem);

        CartItem cartItemOther = new CartItem();
        cartItemOther.setId(cartItemId + 1);
        cartItemOther.setProduct(product);
        cartItemOther.setAmount(2);
        cartItemList.add(cartItemOther);

        cart.setCartItemList(cartItemList);

        CartResponseDTO cartResponseDTOExpected = new CartResponseDTO();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(cartResponseConverter.converToCartResponseDTO(cart)).thenReturn(cartResponseDTOExpected);

        CartResponseDTO cartResponseDTOResult = cartService.removeFromCart(cartItemId, amount);

        assertNotNull(cartResponseDTOResult);
        assertEquals(cartResponseDTOExpected, cartResponseDTOResult);
    }

    @Test
    void it_should_throws_exception_when_remove_and_no_cart() {

        Long cartItemId = 1L;
        Integer amount = 2;

        User user = new User();

        when(userService.getAuthenticateUser()).thenReturn(user);

        assertThatThrownBy(() -> cartService.removeFromCart(cartItemId, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Empty Cart");
    }

    @Test
    void it_should_throws_exception_when_remove_and_no_cart_item() {

        Long cartItemId = 1L;
        Integer amount = 2;

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        List<CartItem> cartItemList = new ArrayList<>();

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId + 1);

        cartItemList.add(cartItem);

        cart.setCartItemList(cartItemList);

        when(userService.getAuthenticateUser()).thenReturn(user);

        assertThatThrownBy(()-> cartService.removeFromCart(cartItemId, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("CartItem not found");
    }

    @Test
    void it_should_fetch_cart_when_cart_exists() {

        User user = new User();
        Cart cart = new Cart();
        user.setCart(cart);

        CartResponseDTO cartResponseExpected = new CartResponseDTO();

        when(userService.getAuthenticateUser()).thenReturn(user);
        when(cartResponseConverter.converToCartResponseDTO(cart)).thenReturn(cartResponseExpected);

        CartResponseDTO cartResponseResult = cartService.fetchCart();

        assertEquals(cartResponseExpected, cartResponseResult);
    }

    @Test
    void it_should_return_null_cart_when_cart_does_not_exist() {

        User user = new User();

        when(userService.getAuthenticateUser()).thenReturn(user);

        CartResponseDTO responseDTO = new CartResponseDTO();

        // when
        CartResponseDTO cartResponseResult = cartService.fetchCart();

        // then
        assertEquals(responseDTO, cartResponseResult);

    }

    @Test
    void test_confirm_cart_success() {

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(1L, cart, new Product(), 2);
        cartItem.getProduct().setId(1L);

        User user = new User();
        user.setCart(cart);

        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);

        cart.setCartItemList(cartItemList);
        cart.setTotalPrice(200F);

        when(userService.getAuthenticateUser()).thenReturn(user);

        ConfirmCartRequest confirmCartRequest = new ConfirmCartRequest();
        CartItemDTO itemDTO = new CartItemDTO(1L, "Samsung A55", 100F, 2);

        confirmCartRequest.setCartItems(Arrays.asList(itemDTO));
        confirmCartRequest.setTotalPrice(200F);

        boolean result = cartService.confirmCart(confirmCartRequest);

        assertTrue(result);
    }

    @Test
    void testConfirmCart_CartNotFound() {

        User user = new User();

        when(userService.getAuthenticateUser()).thenReturn(user);
        ConfirmCartRequest confirmCartRequest = new ConfirmCartRequest();

        boolean result = cartService.confirmCart(confirmCartRequest);

        assertFalse(result);
    }

    @Test
    void testConfirmCart_Item_Size_Mismatch() {

        Cart cart = new Cart();
        CartItem cartItem = new CartItem(1L, cart, new Product(), 2);
        cart.setCartItemList(Arrays.asList(cartItem));

        User user = new User();
        user.setCart(cart);

        when(userService.getAuthenticateUser()).thenReturn(user);

        ConfirmCartRequest confirmCartRequest = new ConfirmCartRequest();
        CartItemDTO itemDTO = new CartItemDTO(1L, "Samsung A55", 100F, 2);
        confirmCartRequest.setCartItems(Arrays.asList(itemDTO, new CartItemDTO(2L, "Samsung A33", 50F,2)));

        boolean result = cartService.confirmCart(confirmCartRequest);

        assertFalse(result);
    }

    @Test
    void testConfirmCart_Price_Mismatch() {

        Cart cart = new Cart();

        CartItem cartItem = new CartItem(1L, cart, new Product(), 2);
        cartItem.getProduct().setId(1L);

        cart.setCartItemList(Arrays.asList(cartItem));
        cart.setTotalPrice(200F);

        User user = new User();
        user.setCart(cart);

        when(userService.getAuthenticateUser()).thenReturn(user);

        ConfirmCartRequest confirmCartRequest = new ConfirmCartRequest();
        CartItemDTO itemDTO = new CartItemDTO(1L, "Samsung A55", 50F, 2);

        confirmCartRequest.setCartItems(Arrays.asList(itemDTO));
        confirmCartRequest.setTotalPrice(100F);

        boolean result = cartService.confirmCart(confirmCartRequest);

        assertFalse(result);
    }

    @Test
    void empty_cart() {

        User user = new User();
        user.setCart(new Cart());

        when(userService.getAuthenticateUser()).thenReturn(user);

        cartService.emptyCart();

        assertEquals(user.getCart(), null);

    }
}


