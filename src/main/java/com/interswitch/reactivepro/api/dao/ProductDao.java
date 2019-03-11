package com.interswitch.reactivepro.api.dao;

import com.interswitch.reactivepro.api.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ProductDao extends ReactiveMongoRepository<Product, String> {
    Flux<Product> findByName(String name);

}
