package com.example.project.service;

import com.example.project.dto.PlantCategoryDto;
import java.util.List;

public interface PlantCategoryService {
    PlantCategoryDto.Response createCategory(PlantCategoryDto.Request request);
    PlantCategoryDto.Response getCategoryById(Long id);
    List<PlantCategoryDto.Response> getAllCategories();
    PlantCategoryDto.Response updateCategory(Long id, PlantCategoryDto.Request request);
    void deleteCategory(Long id);
}
