package com.example.e_commerce.products.service;

import com.example.e_commerce.products.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Product saveProduct(Product product,MultipartFile image) throws IOException;

    List<Product> getAllProducts();

    List<Product> getProductByName(String title);

    Product getProductById(Long id);

    String deleteProduct(Long id);
}
