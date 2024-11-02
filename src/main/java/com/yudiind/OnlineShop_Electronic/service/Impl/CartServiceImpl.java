package com.yudiind.OnlineShop_Electronic.service.Impl;

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
import com.yudiind.OnlineShop_Electronic.service.CartService;
import com.yudiind.OnlineShop_Electronic.service.ProductService;
import com.yudiind.OnlineShop_Electronic.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final CartResponseConverter cartResponseConverter;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, ProductService productService, ProductRepository productRepository, UserService userService, CartResponseConverter cartResponseConverter) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.userService = userService;
        this.cartResponseConverter = cartResponseConverter;
    }

    @Override
    @Transactional
    public CartResponseDTO addToCart(Long productId, Integer amount) {

        User user = userService.getAuthenticateUser();
        Cart cart = user.getCart();

        // Check if the cart and cart items exist
        if (Objects.nonNull(cart) && Objects.nonNull(cart.getCartItemList()) && !cart.getCartItemList().isEmpty()){
            Optional<CartItem> cartItems = cart.getCartItemList()
                    .stream()
                    .filter(ci -> ci.getProduct().getId().equals(productId))
                    .findFirst();

            if (cartItems.isPresent()){
                // Check stock availability
                if (cartItems.get().getProduct().getStock() < (cartItems.get().getAmount() + amount)){
                    throw new InvalidArgumentException("Product does not have desire stock");
                }

            // Update the amount of the existing cart item
            cartItems.get().setAmount(cartItems.get().getAmount() + amount);
            cart.setTotalPrice(calculateTotalPrice(cart));

            // Save the updated cart (it should cascade to cart items)
            cartRepository.save(cart);

            return cartResponseConverter.converToCartResponseDTO(cart);
            }
        }

        // Product not in cart, create a new CartItem
        if (Objects.isNull(cart)){
                cart = createCart(user);
        }

        // Find the product and validate stock
        Product product =  productRepository.findById(productId)
                 .orElseThrow(() -> new InvalidArgumentException("Product not found "));

        if (product.getStock() < amount){
            throw new InvalidArgumentException("Product does not have desired stock ");
        }

        // Create a new cart item for the product
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setAmount(amount);

        // Initialize the cart item list if it's null
        if (Objects.isNull(cart.getCartItemList())){
            cart.setCartItemList(new ArrayList<>());
        }

        // Add the new cart item to the cart and save both cart and cart item
        cart.getCartItemList().add(cartItem);
        cart.setTotalPrice(calculateTotalPrice(cart));

        // Save both cart and cartItem to ensure persistence
        cartRepository.save(cart);      // This should cascade and save CartItem

        return cartResponseConverter.converToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO removeFromCart(Long cartItemId, Integer amount){
        User user = userService.getAuthenticateUser();
        Cart cart = user.getCart();

        if (Objects.isNull(cart) || Objects.isNull(cart.getCartItemList()) || cart.getCartItemList().isEmpty()){
            throw new ResourceNotFoundException("Empty Cart");
        }

        CartItem cartItems = cart.getCartItemList()
                .stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("CartItem not found"));

        if (cartItems.getAmount() <= amount){
            List<CartItem> cartItemList = cart.getCartItemList();
            cartItemList.remove(cartItems);
            if (Objects.isNull(cart.getCartItemList()) || cart.getCartItemList().isEmpty()){
                user.setCart(null);
                userService.saveUser(user);
                return null;
            }
            cart.setCartItemList(cartItemList);
            cart.setTotalPrice(calculateTotalPrice(cart));
            cartRepository.save(cart);
            return cartResponseConverter.converToCartResponseDTO(cart);
        }

        cartItems.setAmount(cartItems.getAmount() - amount);
        cart.setTotalPrice(calculateTotalPrice(cart));
        cartRepository.save(cart);
        return cartResponseConverter.converToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO fetchCart(){
       Cart cart = userService.getAuthenticateUser().getCart();

        // Check if cart is null and return an empty DTO instead of null
       if (cart == null){
           CartResponseDTO emptyCartResponse = new CartResponseDTO();
           return emptyCartResponse;
       }
       return cartResponseConverter.converToCartResponseDTO(cart);

    }

    @Override
    public boolean confirmCart(ConfirmCartRequest confirmCartRequest){

        /**
         - Struktur metode sekarang mengalir secara logis: (1) cek untuk null, (2) cek ukuran item,
           (3) validasi setiap item, (4) validasi total harga.
         - Pengembalian akhir kini hanya bergantung pada satu kondisi: apakah total harga sesuai.
           Jika benar, metode mengembalikan true; jika tidak, sebelumnya sudah dikembalikan false pada pengecekan awal.
         - Kemudahan Pemeliharaan: Kode yang di-refactor lebih mudah dipelihara dan dikembangkan.
           Jika persyaratan di masa depan berubah, seperti penambahan validasi, perubahan menjadi lebih mudah dilakukan.
         */

        Cart dbCart = userService.getAuthenticateUser().getCart();
        // Mengembalikan false lebih awal jika cart null
        if (Objects.isNull(dbCart)){
            return false;
        }

        List<CartItem> dbCartItemList = dbCart.getCartItemList();
        List<CartItemDTO> cartItemDTOList = confirmCartRequest.getCartItems();
        // Mengembalikan false lebih awal jika jumlah item dalam cart berbeda
        if (dbCartItemList.size() != cartItemDTOList.size()){
            return false;
        }

        /**
        for (int i=0; i < dbCartItemList.size(); i++){
            if (!dbCartItemList.get(i).getId().equals(cartItemDTOList.get(i).getId()) &&
                    !dbCartItemList.get(i).getProduct().getId().equals(cartItemDTOList.get(i).getId()) &&
                    !dbCartItemList.get(i).getAmount().equals(cartItemDTOList.get(i).getAmount())) {
                return false;
            }
        }

        if (dbCart.getTotalPrice().equals(confirmCartRequest.getTotalPrice())){
            return true;
        }
         */

        // - Verifikasi setiap item di dalam cart
        // - menggunakan Streams untuk menemukan CartItemDTO yang cocok berdasarkan ID item atau ID produk.
        //   Ini membuat kode lebih deklaratif dan mengurangi potensi kesalahan pada indeks.
        for (CartItem dbCartItem : dbCartItemList) {
            CartItemDTO matchingDTO = cartItemDTOList
                    .stream()
                    .filter(dto -> dto.getId().equals(dbCartItem.getId()) ||
                                   dto.getId().equals(dbCartItem.getProduct().getId()))
                    .findFirst()
                    .orElse(null);

            // Mengembalikan false jika matchingDTO tidak ditemukan atau jumlah tidak sesuai
            if (matchingDTO == null || !matchingDTO.getAmount().equals(dbCartItem.getAmount())){
                return false;
            }
        }

        // validasi total harga
        return dbCart.getTotalPrice().equals(confirmCartRequest.getTotalPrice());
    }

    @Override
    public void emptyCart() {
        User user = userService.getAuthenticateUser();
        user.setCart(null);
        userService.saveUser(user);
    }

    private Float calculateTotalPrice(Cart cart){
        return cart.getCartItemList()
                .stream()
                .map(item -> item.getProduct().getPrice() * item.getAmount())
                .reduce(0f, Float::sum);        // Sum all the mapped values, starting from 0f
    }

    private Cart createCart(User user){
        Cart cart = new Cart();
        cart.setUser(user);
        return cart;
    }
}
