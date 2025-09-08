package com.ecommerce.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.demo.model.Cart;
import com.ecommerce.demo.model.User;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

