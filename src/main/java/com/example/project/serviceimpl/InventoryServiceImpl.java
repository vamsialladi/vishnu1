package com.example.project.serviceimpl;

import com.example.project.dto.InventoryDto;
import com.example.project.entity.Inventory;
import com.example.project.entity.Product;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.InventoryRepository;
import com.example.project.repository.ProductRepository;
import com.example.project.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    private Inventory getOrCreateInventory(Long productId) {
        return inventoryRepository.findByProductIdWithPessimisticLock(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                    Inventory inv = new Inventory(product, 0);
                    return inventoryRepository.save(inv);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDto.Response getInventoryByProductId(Long productId) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
        return mapToResponse(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDto.Response> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDto.Response updateStock(InventoryDto.StockUpdateRequest request) {
        Inventory inv = getOrCreateInventory(request.getProductId());
        int newTotal = inv.getTotalQuantity() + request.getQuantityChange();
        int newAvailable = inv.getAvailableQuantity() + request.getQuantityChange();

        if (newTotal < 0 || newAvailable < 0) {
            throw new IllegalArgumentException("Cannot reduce inventory below zero. Current available: " + inv.getAvailableQuantity());
        }

        inv.setTotalQuantity(newTotal);
        inv.setAvailableQuantity(newAvailable);
        inv.setUpdatedAt(LocalDateTime.now());

        Inventory saved = inventoryRepository.save(inv);
        return mapToResponse(saved);
    }

    @Override
    public boolean reserveStock(Long productId, int quantity) {
        if (quantity <= 0) return true;
        Inventory inv = getOrCreateInventory(productId);
        if (inv.getAvailableQuantity() < quantity) {
            return false;
        }
        inv.setAvailableQuantity(inv.getAvailableQuantity() - quantity);
        inv.setReservedQuantity(inv.getReservedQuantity() + quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
        return true;
    }

    @Override
    public void releaseStock(Long productId, int quantity) {
        if (quantity <= 0) return;
        Inventory inv = getOrCreateInventory(productId);
        int releaseCount = Math.min(inv.getReservedQuantity(), quantity);
        inv.setReservedQuantity(inv.getReservedQuantity() - releaseCount);
        inv.setAvailableQuantity(inv.getAvailableQuantity() + releaseCount);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    @Override
    public void confirmInstallation(Long productId, int quantity, boolean isRental) {
        if (quantity <= 0) return;
        Inventory inv = getOrCreateInventory(productId);
        int deductFromReserved = Math.min(inv.getReservedQuantity(), quantity);
        inv.setReservedQuantity(inv.getReservedQuantity() - deductFromReserved);
        if (isRental) {
            inv.setRentalQuantity(inv.getRentalQuantity() + quantity);
        } else {
            inv.setInstalledQuantity(inv.getInstalledQuantity() + quantity);
        }
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long productId, int quantity) {
        return inventoryRepository.findByProductId(productId)
                .map(inv -> inv.getAvailableQuantity() >= quantity)
                .orElse(false);
    }

    private InventoryDto.Response mapToResponse(Inventory inv) {
        InventoryDto.Response resp = new InventoryDto.Response();
        resp.setId(inv.getId());
        resp.setProductId(inv.getProduct().getId());
        resp.setProductName(inv.getProduct().getName());
        resp.setProductSku(inv.getProduct().getSku());
        resp.setTotalQuantity(inv.getTotalQuantity());
        resp.setAvailableQuantity(inv.getAvailableQuantity());
        resp.setReservedQuantity(inv.getReservedQuantity());
        resp.setInstalledQuantity(inv.getInstalledQuantity());
        resp.setRentalQuantity(inv.getRentalQuantity());
        resp.setDamagedQuantity(inv.getDamagedQuantity());
        resp.setUpdatedAt(inv.getUpdatedAt());
        return resp;
    }
}
