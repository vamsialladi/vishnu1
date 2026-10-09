package com.example.project.serviceimpl;

import com.example.project.dto.PlantCategoryDto;
import com.example.project.entity.PlantCategory;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.PlantCategoryRepository;
import com.example.project.service.PlantCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlantCategoryServiceImpl implements PlantCategoryService {

    private final PlantCategoryRepository categoryRepository;

    public PlantCategoryServiceImpl(PlantCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PlantCategoryDto.Response createCategory(PlantCategoryDto.Request request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category already exists with name: " + request.getName());
        }
        PlantCategory category = new PlantCategory(request.getName(), request.getDescription(), null);
        PlantCategory saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantCategoryDto.Response getCategoryById(Long id) {
        PlantCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantCategoryDto.Response> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PlantCategoryDto.Response updateCategory(Long id, PlantCategoryDto.Request request) {
        PlantCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        PlantCategory saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        PlantCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    private PlantCategoryDto.Response mapToResponse(PlantCategory category) {
        PlantCategoryDto.Response resp = new PlantCategoryDto.Response();
        resp.setId(category.getId());
        resp.setName(category.getName());
        resp.setDescription(category.getDescription());
        return resp;
    }
}
