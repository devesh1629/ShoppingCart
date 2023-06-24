package com.microservice.productservice.service.impl;

import com.microservice.productservice.entity.Product;
import com.microservice.productservice.exception.ProductServiceCustomException;
import com.microservice.productservice.payload.request.ProductRequest;
import com.microservice.productservice.payload.response.ProductResponse;
import com.microservice.productservice.repository.ProductRepository;
import com.microservice.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("ProductServiceImpl | addProduct is called");

        Product product = Product.builder()
            .productName(productRequest.getName())
            .price(productRequest.getPrice())
            .quantity(productRequest.getQuantity())
            .build();
        product = productRepository.save(product);

        log.info("ProductServiceImpl | addProduct | Product Created");
        log.info("ProductServiceImpl | addProduct | Product Id :" + product.getProductId());
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("ProductServiceImpl | getProductById is called");
        log.info("ProductServiceImpl | Get the product for productId: {}", productId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductServiceCustomException(
                    "Product with given id not found",
                    "PRODUCT_NOT_FOUND"
            ));
        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);
        log.info("ProductServiceImpl | getProductById | productResponse :" + productResponse.toString());
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {}", quantity, productId);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductServiceCustomException(
                    "Product with given id not found",
                    "PRODUCT_NOT_FOUND"
            ));
        if(product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                "Product does not have sufficient quantity",
                "INSUFFICIENT_QUANTITY"
            );
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product quantity updated successfully");
    }

    @Override
    public void deleteProductById(long productId) {
        log.info("Delete product id: {}", productId);

        if(!productRepository.existsById(productId)) {
            log.info("Product not found for deleting: {}", productId);
            throw new ProductServiceCustomException(
                "Product with given id not found",
                "PRODUCT_NOT_FOUND"
            );
        }
        log.info("Deleting product with id: {}", productId);
        productRepository.deleteById(productId);
    }
}
