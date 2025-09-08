package com.ecommerce.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 1000)
    private String description;
    private String category;
    private Double price;

    public Item(String name, String description, String category, Double price) {
        this.name = name; this.description = description; this.category = category; this.price = price;
    }
}

