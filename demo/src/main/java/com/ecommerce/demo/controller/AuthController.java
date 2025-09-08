package com.ecommerce.demo.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.ecommerce.demo.repository.UserRepository;
import com.ecommerce.demo.model.User;
import com.ecommerce.demo.security.JwtUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwt;

    public AuthController(UserRepository userRepo, BCryptPasswordEncoder encoder, AuthenticationManager authManager, JwtUtils jwt) {
        this.userRepo = userRepo; this.encoder = encoder; this.authManager = authManager; this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        if (userRepo.existsByUsername(username)) return ResponseEntity.badRequest().body(Map.of("error","Username taken"));
        if (userRepo.existsByEmail(email)) return ResponseEntity.badRequest().body(Map.of("error","Email taken"));
        User u = new User(username, email, encoder.encode(password));
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("message","User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = jwt.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token, "username", username));
    }
}

