package com.example.e_commerce.products.controller;

import com.example.e_commerce.products.entity.Product;
import com.example.e_commerce.products.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping(value = "/postProducts",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProduct(
            @RequestPart() Product product,
            @RequestPart() MultipartFile image
    ) throws IOException {
        try {
            return new ResponseEntity<>(productService.saveProduct(product, image), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.OK);
    }

    @GetMapping("/getByTitle/{product}")
    public ResponseEntity<List<Product>> getProductById(@PathVariable String product) {
        return new ResponseEntity<>(productService.getProductByName(product),HttpStatus.OK);
    }

    @GetMapping("/getById/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProductById(productId),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<String> deleteDetails(@PathVariable Long Id) {
        try {
            String d = productService.deleteProduct(Id);
            return new ResponseEntity<>(d, HttpStatus.OK);
        } catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }
    }
}
