package com.ecommerce.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.demo.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryContainingIgnoreCase(String category);
}

