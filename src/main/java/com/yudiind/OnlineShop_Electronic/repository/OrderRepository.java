package com.yudiind.OnlineShop_Electronic.repository;

import com.yudiind.OnlineShop_Electronic.model.entity.Order;
import com.yudiind.OnlineShop_Electronic.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByUserOrderByDateDesc(User user, Pageable pageable);

    Optional<Integer> countAllByUser(User user);


}
