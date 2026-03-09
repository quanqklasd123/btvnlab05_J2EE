package com.example.lab05_j2ee.service;

import com.example.lab05_j2ee.model.Category;
import com.example.lab05_j2ee.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Lấy danh sách tất cả danh mục
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Lưu danh mục (thêm mới hoặc cập nhật)
     */
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    /**
     * Tìm danh mục theo ID
     */
    public Category getCategoryById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    /**
     * Xóa danh mục theo ID
     */
    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }
}
