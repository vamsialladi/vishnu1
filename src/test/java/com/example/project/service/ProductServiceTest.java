package com.example.project.service;

import com.example.project.dto.ProductRequest;
import com.example.project.dto.ProductResponse;
import com.example.project.entity.Product;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.ProductRepository;
import com.example.project.serviceimpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("Peace Lily", "Spathiphyllum indoor flowering plant", new BigDecimal("34.99"), 25, "Flowering Plants", "PLANT-LILY1");
        sampleProduct.setId(1L);

        productRequest = new ProductRequest("Peace Lily", "Spathiphyllum indoor flowering plant", new BigDecimal("34.99"), 25, "Flowering Plants", "PLANT-LILY1");
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct() {
        when(productRepository.existsBySku("PLANT-LILY1")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        ProductResponse response = productService.createProduct(productRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Peace Lily");
        assertThat(response.getSku()).isEqualTo("PLANT-LILY1");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with ID: 99");
    }

    @Test
    @DisplayName("Should find products by category")
    void shouldFindProductsByCategory() {
        when(productRepository.findByCategory("Flowering Plants")).thenReturn(List.of(sampleProduct));

        List<ProductResponse> products = productService.getProductsByCategory("Flowering Plants");

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getCategory()).isEqualTo("Flowering Plants");
    }
}
