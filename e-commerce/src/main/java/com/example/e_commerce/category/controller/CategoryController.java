package com.example.e_commerce.category.controller;

import com.example.e_commerce.category.entity.Category;
import com.example.e_commerce.category.service.CategoryService;
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
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Category>> getDetails(){
        return new ResponseEntity<>(categoryService.GetDetails(), HttpStatus.OK);
    }

    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postDetails(
            @RequestPart() Category category,
            @RequestPart() MultipartFile image
    ) throws IOException {
        try {
            return new ResponseEntity<>(categoryService.PostDetails(category, image), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteDetails(@PathVariable Long categoryId) {
        try {
            String d = categoryService.DeleteCategory(categoryId);
            return new ResponseEntity<>(d, HttpStatus.OK);
        } catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }
    }

    @PutMapping("/put/{studentId}")
    public ResponseEntity<String> putDetails(
            @RequestBody Category categoryDetails,
            @PathVariable Long categoryId,
            @RequestPart() MultipartFile image
    ){
        try{
            Category d=categoryService.PutDetails(categoryDetails,categoryId,image);
            return new ResponseEntity<>("student with studentId "+categoryId+" is modified",HttpStatus.OK);
        }
        catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
