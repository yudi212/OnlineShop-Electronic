package com.yudiind.OnlineShop_Electronic.repository;

import com.yudiind.OnlineShop_Electronic.model.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    List<Product> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
