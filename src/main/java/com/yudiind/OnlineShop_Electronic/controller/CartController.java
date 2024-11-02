package com.yudiind.OnlineShop_Electronic.controller;

import com.yudiind.OnlineShop_Electronic.model.dto.CartResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ConfirmCartRequest;
import com.yudiind.OnlineShop_Electronic.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestParam Long productId,
                                                     @RequestParam Integer amount){

        CartResponseDTO cartResponseDTO = cartService.addToCart(productId, amount);
        return ResponseEntity.ok(cartResponseDTO);
    }

    @PostMapping("/remove")
    public ResponseEntity<CartResponseDTO> removeFromCart(@RequestParam Long cartItemId,
                                                          @RequestParam Integer amount){

        CartResponseDTO responseDTO = cartService.removeFromCart(cartItemId, amount);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/fetch")
    public ResponseEntity<CartResponseDTO> fetchCart(){
        CartResponseDTO responseDTO = cartService.fetchCart();
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/empty")
    public ResponseEntity<HttpStatus> emptyCart(){
        cartService.emptyCart();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Boolean> confirmCart(@RequestBody @Valid ConfirmCartRequest cartRequest){
        Boolean confirmCart = cartService.confirmCart(cartRequest);
        return ResponseEntity.ok(confirmCart);
    }
}
