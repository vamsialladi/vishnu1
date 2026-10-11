package com.example.project.service;

import com.example.project.dto.CartDto;
import com.example.project.entity.Cart;
import com.example.project.entity.CartItem;
import com.example.project.entity.Product;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.CartItemRepository;
import com.example.project.repository.CartRepository;
import com.example.project.repository.MaintenancePlanRepository;
import com.example.project.repository.ProductRepository;
import com.example.project.repository.UserRepository;
import com.example.project.serviceimpl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MaintenancePlanRepository maintenancePlanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User("alice", "alice@example.com", "pass", "Alice Smith", Role.ROLE_CUSTOMER);
        user.setId(1L);

        product = new Product("Fiddle Leaf Fig", "Indoor fig", BigDecimal.valueOf(1000), 10, "Indoor", "SKU-FIG-01");
        product.setId(2L);
        product.setInstallationCharge(BigDecimal.valueOf(150));
        product.setRentalPrice(BigDecimal.valueOf(200));

        cart = new Cart(user);
        cart.setId(5L);
    }

    @Test
    @DisplayName("Should add item to cart and calculate correct totals")
    void shouldAddItemToCart() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartItem savedItem = new CartItem(cart, product, 2, false, true, null);
        savedItem.setId(100L);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);

        CartDto.AddItemRequest request = new CartDto.AddItemRequest();
        request.setProductId(2L);
        request.setQuantity(2);
        request.setRental(false);
        request.setIncludeInstallation(true);

        CartDto.Response response = cartService.addToCart(request, "alice@example.com");

        assertThat(response).isNotNull();
        // 2 items * 1000 = 2000 subtotal
        // installation: 2 * 150 = 300
        // total before tax = 2300, tax 18% = 414.00, grand total = 2714.00
        assertThat(response.getSubtotal()).isEqualByComparingTo("2000.00");
        assertThat(response.getInstallationCharge()).isEqualByComparingTo("300.00");
        assertThat(response.getTax()).isEqualByComparingTo("414.00");
        assertThat(response.getGrandTotal()).isEqualByComparingTo("2714.00");
    }

    @Test
    @DisplayName("Should clear all items from user cart")
    void shouldClearCart() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart("alice@example.com");

        verify(cartItemRepository, times(1)).deleteByCartId(5L);
    }
}
