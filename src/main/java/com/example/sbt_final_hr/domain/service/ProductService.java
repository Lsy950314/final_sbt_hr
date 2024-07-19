package com.example.sbt_final_hr.domain.service;


import com.example.sbt_final_hr.domain.model.dto.ProductRequest;
import com.example.sbt_final_hr.domain.model.entity.Product;
import com.example.sbt_final_hr.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private static ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //c
    public void saveProduct(ProductRequest productRequest) {
        productRepository.save(productRequest.toEntity());
    }
    //r
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //u
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    //d
    public void deleteById(ProductRequest productRequest) {
        productRepository.deleteById(productRequest.getId());
    }

}
