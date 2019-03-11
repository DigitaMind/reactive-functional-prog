package com.interswitch.reactivepro.api.handler;

import com.interswitch.reactivepro.api.dao.ProductDao;
import com.interswitch.reactivepro.api.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductHandler {

    @Autowired
    private ProductDao productDao;

    public Mono<ServerResponse> getAllProducts(ServerRequest request){
        Flux<Product> products = productDao.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(products, Product.class);

    }

    public Mono<ServerResponse> getProductById(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<Product> product = productDao.findById(id);

        return product.flatMap(p ->
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromObject(product))
        )
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> saveProduct(ServerRequest request){
        Mono<Product> productMono = request.bodyToMono(Product.class);
        return productMono.flatMap(product ->
                ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productDao.save(product), Product.class));

    }

    public Mono<ServerResponse> updateProduct(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<Product> existingProductMono = productDao.findById(id);
        Mono<Product> productMono = request.bodyToMono(Product.class);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        return productMono.zipWith(existingProductMono,
                (product, existingProduct) -> new Product(existingProduct.getId(), product.getName(), product.getPrice()))
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productDao.save(product), Product.class))
                .switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<Product> productMono = productDao.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        return productMono.flatMap(existingProduct -> ServerResponse.ok()
                .build(productDao.delete(existingProduct))
                .switchIfEmpty(notFound));

    }
}
