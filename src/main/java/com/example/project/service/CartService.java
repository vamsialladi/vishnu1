package com.example.project.service;

import com.example.project.dto.CartDto;

public interface CartService {
    CartDto.Response getCart(String userEmail);
    CartDto.Response addToCart(CartDto.AddItemRequest request, String userEmail);
    CartDto.Response updateCartItem(Long itemId, CartDto.UpdateItemRequest request, String userEmail);
    CartDto.Response removeFromCart(Long itemId, String userEmail);
    void clearCart(String userEmail);
}
