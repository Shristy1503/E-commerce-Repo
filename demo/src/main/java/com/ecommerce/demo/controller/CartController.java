package com.ecommerce.demo.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ecommerce.demo.model.User;
import com.ecommerce.demo.model.Cart;
import com.ecommerce.demo.model.CartItem;
import com.ecommerce.demo.model.Item;

import com.ecommerce.demo.repository.UserRepository;
import com.ecommerce.demo.repository.CartRepository;
import com.ecommerce.demo.repository.CartItemRepository;
import com.ecommerce.demo.repository.ItemRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final UserRepository userRepo;
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ItemRepository itemRepo;

    public CartController(UserRepository userRepo, CartRepository cartRepo, CartItemRepository cartItemRepo, ItemRepository itemRepo) {
        this.userRepo = userRepo; this.cartRepo = cartRepo; this.cartItemRepo = cartItemRepo; this.itemRepo = itemRepo;
    }

    private Optional<User> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username);
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        Optional<User> uopt = getCurrentUser();
        if (uopt.isEmpty()) return ResponseEntity.status(401).body("Not authenticated");
        User user = uopt.get();
        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart c = new Cart(); c.setUser(user); return cartRepo.save(c);
        });
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddRequest req) {
        Optional<User> uopt = getCurrentUser();
        if (uopt.isEmpty()) return ResponseEntity.status(401).body("Not authenticated");
        User user = uopt.get();
        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart c = new Cart(); c.setUser(user); return cartRepo.save(c);
        });

        Item item = itemRepo.findById(req.itemId).orElse(null);
        if (item == null) return ResponseEntity.badRequest().body("Item not found");

        // find existing cart item
        CartItem match = cart.getItems().stream().filter(ci -> ci.getItem().getId().equals(item.getId())).findFirst().orElse(null);
        if (match == null) {
            CartItem ci = new CartItem(); ci.setItem(item); ci.setQuantity(req.quantity); ci.setCart(cart);
            cart.getItems().add(cartItemRepo.save(ci));
        } else {
            match.setQuantity(match.getQuantity() + req.quantity);
            cartItemRepo.save(match);
        }
        cartRepo.save(cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestBody AddRequest req) {
        Optional<User> uopt = getCurrentUser();
        if (uopt.isEmpty()) return ResponseEntity.status(401).body("Not authenticated");
        User user = uopt.get();
        Cart cart = cartRepo.findByUser(user).orElse(null);
        if (cart == null) return ResponseEntity.badRequest().body("Cart empty");

        CartItem match = cart.getItems().stream().filter(ci -> ci.getItem().getId().equals(req.itemId)).findFirst().orElse(null);
        if (match == null) return ResponseEntity.badRequest().body("Item not in cart");
        if (req.quantity >= match.getQuantity()) {
            cart.getItems().remove(match);
            cartItemRepo.delete(match);
        } else {
            match.setQuantity(match.getQuantity() - req.quantity);
            cartItemRepo.save(match);
        }
        cartRepo.save(cart);
        return ResponseEntity.ok(cart);
    }

    static class AddRequest { public Long itemId; public int quantity; }
}
