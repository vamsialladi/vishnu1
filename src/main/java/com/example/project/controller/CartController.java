package com.example.project.controller;

import com.example.project.dto.CartDto;
import com.example.project.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDto.Response> getCart(@RequestParam String username) {
        return ResponseEntity.ok(cartService.getCart(username));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto.Response> addToCart(@Valid @RequestBody CartDto.AddItemRequest request,
                                                      @RequestParam String username) {
        return ResponseEntity.ok(cartService.addToCart(request, username));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDto.Response> updateCartItem(@PathVariable Long itemId,
                                                           @Valid @RequestBody CartDto.UpdateItemRequest request,
                                                           @RequestParam String username) {
        return ResponseEntity.ok(cartService.updateCartItem(itemId, request, username));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDto.Response> removeFromCart(@PathVariable Long itemId,
                                                           @RequestParam String username) {
        return ResponseEntity.ok(cartService.removeFromCart(itemId, username));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam String username) {
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }
}
