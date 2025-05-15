package com.example.e_commerce.category.service;

import com.example.e_commerce.category.entity.Category;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    List<Category> GetDetails();

    Category PostDetails(Category details, MultipartFile image) throws IOException;

    String DeleteCategory(Long studentId);

    Category PutDetails(Category studentDetails, Long studentId, MultipartFile image) throws IOException;
}
