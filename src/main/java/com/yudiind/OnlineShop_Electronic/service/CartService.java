package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.CartResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ConfirmCartRequest;

public interface CartService {

    CartResponseDTO addToCart(Long productId, Integer amount);

    CartResponseDTO removeFromCart(Long cartItemId, Integer amount);

    CartResponseDTO fetchCart();

    boolean confirmCart(ConfirmCartRequest confirmCartRequest);

    void emptyCart();
}
