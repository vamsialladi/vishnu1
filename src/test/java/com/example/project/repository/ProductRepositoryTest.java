package com.example.project.repository;

import com.example.project.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save and find product by SKU")
    void shouldSaveAndFindBySku() {
        Product product = new Product("Monstera Deliciosa", "Swiss cheese plant", new BigDecimal("49.99"), 30, "Indoor Plants", "PLANT-MONS1");
        productRepository.save(product);

        Optional<Product> found = productRepository.findBySku("PLANT-MONS1");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Monstera Deliciosa");
    }

    @Test
    @DisplayName("Should search products by name or description keyword")
    void shouldSearchProducts() {
        Product p1 = new Product("Snake Plant", "Sansevieria trifasciata air purifier", new BigDecimal("29.99"), 40, "Indoor Plants", "PLANT-SNAKE");
        Product p2 = new Product("Fiddle Leaf Fig", "Ficus lyrata ornamental tree", new BigDecimal("89.99"), 15, "Indoor Trees", "PLANT-FIG");
        productRepository.save(p1);
        productRepository.save(p2);

        List<Product> results = productRepository.searchProducts("purifier");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Snake Plant");
    }
}
