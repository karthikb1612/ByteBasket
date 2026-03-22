package com.example.e_commerce.category.service;

import com.example.e_commerce.category.entity.Category;
import com.example.e_commerce.category.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepo categoryRepo;

    @Override
    public List<Category> GetDetails() {
        return categoryRepo.findAll();
    }

    @Override
    public Category PostDetails(Category details, MultipartFile image) throws IOException {
        details.setImageName(image.getOriginalFilename());
        details.setImageType(image.getContentType());
        details.setImageData(image.getBytes());
        return categoryRepo.save(details);
    }

    @Override
    public String DeleteCategory(Long studentId) {
        Category details=categoryRepo.findById(studentId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not found"));
        categoryRepo.delete(details);
        return "student with studentId "+details.getCategoryId()+" is deleted";
    }

    @Override
    public Category PutDetails(Category studentDetails, Long studentId, MultipartFile image) throws IOException {
        Category details=categoryRepo.findById(studentId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not found"));
        studentDetails.setCategoryId(studentId);
        studentDetails.setImageName(image.getOriginalFilename());
        studentDetails.setImageType(image.getContentType());
        studentDetails.setImageData(image.getBytes());
        details=categoryRepo.save(studentDetails);
        return details;
    }
}
