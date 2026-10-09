package com.example.project.controller;

import com.example.project.dto.ProductRequest;
import com.example.project.dto.ProductResponse;
import com.example.project.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Public GET /api/v1/products should return product list")
    void shouldGetAllProducts() throws Exception {
        ProductResponse response = new ProductResponse(
                1L, "Desk Palm", "Chamaedorea elegans", "Compact indoor palm", new BigDecimal("39.99"),
                new BigDecimal("15.00"), new BigDecimal("10.00"), 50, "Palms", "PLANT-PALM1",
                "Medium Indirect", "Weekly", "Low", true, true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(productService.getAllProducts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Desk Palm"))
                .andExpect(jsonPath("$[0].sku").value("PLANT-PALM1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Admin POST /api/v1/products should create product")
    void shouldCreateProductWhenAdmin() throws Exception {
        ProductRequest request = new ProductRequest("Desk Palm", "Compact indoor palm", new BigDecimal("39.99"), 50, "Palms", "PLANT-PALM1");
        ProductResponse response = new ProductResponse(
                1L, "Desk Palm", "Chamaedorea elegans", "Compact indoor palm", new BigDecimal("39.99"),
                new BigDecimal("15.00"), new BigDecimal("10.00"), 50, "Palms", "PLANT-PALM1",
                "Medium Indirect", "Weekly", "Low", true, true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Desk Palm"));
    }
}
