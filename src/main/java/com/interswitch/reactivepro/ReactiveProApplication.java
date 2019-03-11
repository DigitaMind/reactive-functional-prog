package com.interswitch.reactivepro;

import com.interswitch.reactivepro.api.dao.ProductDao;
import com.interswitch.reactivepro.api.model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveProApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveProApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ReactiveMongoOperations operations, ProductDao productDao){
		return args -> {
			Flux<Product> productFlux = Flux.just(
					new Product(null, "Memory Card", 5000.00),
					new Product(null, "Jotter", 2000.00),
					new Product(null, "Flash Card", 5000.00)
			)
					.flatMap(productDao::save);
			productFlux
					.thenMany(productDao.findAll())
					.subscribe(System.out::println);
			/*
			operations.collectionExists(Product.class)
					.flatMap(exists -> exists? operations.dropCollection(Product.class) : Mono.just(exists))
					.thenMany(v -> operations.createCollection(Product.class))
					.thenMany(productFlux)
					.thenMany(productDao.findAll())
					.subscribe(System.out::println);
			*/

		};
	}
}
