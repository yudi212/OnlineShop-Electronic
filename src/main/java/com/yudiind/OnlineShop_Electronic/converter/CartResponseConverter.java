package com.yudiind.OnlineShop_Electronic.converter;

import com.yudiind.OnlineShop_Electronic.model.dto.CartItemResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.CartResponseDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.Cart;
import com.yudiind.OnlineShop_Electronic.model.entity.Image;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartResponseConverter {
  public CartResponseDTO converToCartResponseDTO(Cart cart){

            List<CartItemResponseDTO> items = cart.getCartItemList()
                    .stream()
                    .map(item -> {
                      CartItemResponseDTO dto = new CartItemResponseDTO();
                      dto.setProductId(item.getProduct().getId());
                      dto.setProductName(item.getProduct().getName());
                      dto.setAmount(item.getAmount());
                      dto.setPrice(item.getProduct().getPrice());

                      List<Image> images = item.getProduct().getImages();
                      if (images != null && !images.isEmpty()){
                          dto.setImageUrl("/image" + images.get(0).getFileName());
                      } else {
                          dto.setImageUrl(null);    // No image available
                      }

                      return dto;
                    })
                    .collect(Collectors.toList());

            CartResponseDTO response = new CartResponseDTO();
            response.setCartId(cart.getId());
            response.setItems(items);
            response.setTotalPrice(cart.getTotalPrice());
            return response;
  }

}
