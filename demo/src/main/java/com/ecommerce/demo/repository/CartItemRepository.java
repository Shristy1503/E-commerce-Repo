package com.ecommerce.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.demo.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> { }

