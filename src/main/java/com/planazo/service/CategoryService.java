package com.planazo.service;

import com.planazo.dto.response.CategoryResponse;
import com.planazo.model.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getActiveCategories();

    CategoryResponse getCategoryById(Long id);

    Category getCategoryEntityById(Long id);
}
