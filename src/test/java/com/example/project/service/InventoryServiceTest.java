package com.example.project.service;

import com.example.project.dto.InventoryDto;
import com.example.project.entity.Inventory;
import com.example.project.entity.Product;
import com.example.project.repository.InventoryRepository;
import com.example.project.repository.ProductRepository;
import com.example.project.serviceimpl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product product;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        product = new Product("Snake Plant", "Indoor Plant", BigDecimal.valueOf(500), 20, "Indoor", "SKU-SNAKE-01");
        product.setId(1L);

        inventory = new Inventory(product, 20);
        inventory.setId(10L);
    }

    @Test
    @DisplayName("Should successfully reserve available stock")
    void shouldReserveStock() {
        when(inventoryRepository.findByProductIdWithPessimisticLock(1L)).thenReturn(Optional.of(inventory));

        boolean reserved = inventoryService.reserveStock(1L, 5);

        assertThat(reserved).isTrue();
        assertThat(inventory.getAvailableQuantity()).isEqualTo(15);
        assertThat(inventory.getReservedQuantity()).isEqualTo(5);
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    @DisplayName("Should fail to reserve stock when requested quantity exceeds available")
    void shouldFailReserveWhenExceedsAvailable() {
        when(inventoryRepository.findByProductIdWithPessimisticLock(1L)).thenReturn(Optional.of(inventory));

        boolean reserved = inventoryService.reserveStock(1L, 25);

        assertThat(reserved).isFalse();
        assertThat(inventory.getAvailableQuantity()).isEqualTo(20);
        assertThat(inventory.getReservedQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should prevent negative inventory on stock update")
    void shouldPreventNegativeInventory() {
        when(inventoryRepository.findByProductIdWithPessimisticLock(1L)).thenReturn(Optional.of(inventory));

        InventoryDto.StockUpdateRequest req = new InventoryDto.StockUpdateRequest();
        req.setProductId(1L);
        req.setQuantityChange(-25); // more than 20 total

        assertThatThrownBy(() -> inventoryService.updateStock(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    @Test
    @DisplayName("Should release reserved stock")
    void shouldReleaseStock() {
        inventory.setReservedQuantity(5);
        inventory.setAvailableQuantity(15);
        when(inventoryRepository.findByProductIdWithPessimisticLock(1L)).thenReturn(Optional.of(inventory));

        inventoryService.releaseStock(1L, 3);

        assertThat(inventory.getReservedQuantity()).isEqualTo(2);
        assertThat(inventory.getAvailableQuantity()).isEqualTo(18);
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    @DisplayName("Should check stock availability correctly")
    void shouldCheckAvailability() {
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        assertThat(inventoryService.checkAvailability(1L, 10)).isTrue();
        assertThat(inventoryService.checkAvailability(1L, 30)).isFalse();
    }
}
