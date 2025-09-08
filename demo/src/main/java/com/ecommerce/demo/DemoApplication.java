package com.ecommerce.demo;

import com.ecommerce.demo.model.Item;
import com.ecommerce.demo.model.User;
import com.ecommerce.demo.repository.ItemRepository;
import com.ecommerce.demo.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(ItemRepository itemRepo, UserRepository userRepo, BCryptPasswordEncoder encoder) {
		return args -> {
			if (itemRepo.count() == 0) {
				itemRepo.save(new Item("Blue T-Shirt","Comfortable cotton tee","Apparel",299.0));
				itemRepo.save(new Item("Running Shoes","Lightweight sneakers","Footwear",1999.0));
				itemRepo.save(new Item("Coffee Mug","Ceramic mug 300ml","Home",249.0));
				itemRepo.save(new Item("Wireless Mouse","Ergonomic Bluetooth mouse","Electronics",899.0));
			}
			if (userRepo.findByUsername("test").isEmpty()) {
				User u = new User("test","test@example.com", encoder.encode("test123"));
				userRepo.save(u);
			}
		};
	}
}
