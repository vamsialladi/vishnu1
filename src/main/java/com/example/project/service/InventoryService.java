package com.example.project.service;

import com.example.project.dto.InventoryDto;
import java.util.List;

public interface InventoryService {
    InventoryDto.Response getInventoryByProductId(Long productId);
    List<InventoryDto.Response> getAllInventory();
    InventoryDto.Response updateStock(InventoryDto.StockUpdateRequest request);
    boolean reserveStock(Long productId, int quantity);
    void releaseStock(Long productId, int quantity);
    void confirmInstallation(Long productId, int quantity, boolean isRental);
    boolean checkAvailability(Long productId, int quantity);
}
