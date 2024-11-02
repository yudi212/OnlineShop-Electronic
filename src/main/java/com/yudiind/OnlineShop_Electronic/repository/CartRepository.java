package com.yudiind.OnlineShop_Electronic.repository;

import com.yudiind.OnlineShop_Electronic.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
