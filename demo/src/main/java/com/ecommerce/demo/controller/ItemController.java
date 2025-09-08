package com.ecommerce.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.ecommerce.demo.model.Item;
import com.ecommerce.demo.repository.ItemRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemRepository itemRepo;
    public ItemController(ItemRepository itemRepo) { this.itemRepo = itemRepo; }

    @GetMapping
    public List<Item> list(
            @RequestParam(required=false) String category,
            @RequestParam(required=false) Double minPrice,
            @RequestParam(required=false) Double maxPrice
    ) {
        List<Item> list = itemRepo.findAll();
        if (category != null && !category.isBlank()) {
            list.removeIf(i -> !i.getCategory().toLowerCase().contains(category.toLowerCase()));
        }
        if (minPrice != null) list.removeIf(i -> i.getPrice() < minPrice);
        if (maxPrice != null) list.removeIf(i -> i.getPrice() > maxPrice);
        return list;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> get(@PathVariable Long id) {
        return itemRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Item create(@RequestBody Item item) { return itemRepo.save(item); }

    @PutMapping("/{id}")
    public ResponseEntity<Item> update(@PathVariable Long id, @RequestBody Item item) {
        return itemRepo.findById(id).map(existing -> {
            existing.setName(item.getName());
            existing.setDescription(item.getDescription());
            existing.setCategory(item.getCategory());
            existing.setPrice(item.getPrice());
            itemRepo.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!itemRepo.existsById(id)) return ResponseEntity.notFound().build();
        itemRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
