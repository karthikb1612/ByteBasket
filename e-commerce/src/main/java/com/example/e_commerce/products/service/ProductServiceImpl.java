package com.example.e_commerce.products.service;

import com.example.e_commerce.products.entity.Product;
import com.example.e_commerce.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product, MultipartFile image) throws IOException {
        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setImageData(image.getBytes());
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductByName(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public Product getProductById(Long id) {
       return productRepository.findById(id).
               orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not found"));
    }

    @Override
    public String deleteProduct(Long id) {
        productRepository.deleteById(id);
        return "Product with "+id+" is deleted";
    }
}
