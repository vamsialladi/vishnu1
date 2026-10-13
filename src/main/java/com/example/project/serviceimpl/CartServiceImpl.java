package com.example.project.serviceimpl;

import com.example.project.dto.CartDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final MaintenancePlanRepository maintenancePlanRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           MaintenancePlanRepository maintenancePlanRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.maintenancePlanRepository = maintenancePlanRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto.Response getCart(String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);
        return mapToCartResponse(cart);
    }

    @Override
    public CartDto.Response addToCart(CartDto.AddItemRequest request, String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        MaintenancePlan plan = null;
        if (request.getMaintenancePlanId() != null) {
            plan = maintenancePlanRepository.findById(request.getMaintenancePlanId()).orElse(null);
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) && item.isRental() == request.isRental())
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            if (plan != null) existingItem.setMaintenancePlan(plan);
            existingItem.setIncludeInstallation(request.isIncludeInstallation());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, product, request.getQuantity(), request.isRental());
            newItem.setIncludeInstallation(request.isIncludeInstallation());
            newItem.setMaintenancePlan(plan);
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        Cart updatedCart = cartRepository.save(cart);
        return mapToCartResponse(updatedCart);
    }

    @Override
    public CartDto.Response updateCartItem(Long itemId, CartDto.UpdateItemRequest request, String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user cart");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return mapToCartResponse(cart);
    }

    @Override
    public CartDto.Response removeFromCart(Long itemId, String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user cart");
        }

        cart.removeItem(item);
        cartItemRepository.delete(item);

        return mapToCartResponse(cart);
    }

    @Override
    public void clearCart(String userEmail) {
        User user = getUser(userEmail);
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
    }

    private CartDto.Response mapToCartResponse(Cart cart) {
        CartDto.Response resp = new CartDto.Response();
        resp.setId(cart.getId());
        resp.setUserId(cart.getUser().getId());

        List<CartDto.ItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalInstallation = BigDecimal.ZERO;
        BigDecimal totalMaintenance = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            CartDto.ItemResponse itemDto = new CartDto.ItemResponse();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setProductSku(item.getProduct().getSku());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setRental(item.isRental());

            BigDecimal unitPrice = item.isRental() ? item.getProduct().getRentalPrice() : item.getProduct().getPrice();
            BigDecimal installCharge = item.isIncludeInstallation() ? item.getProduct().getInstallationCharge() : BigDecimal.ZERO;
            BigDecimal maintCharge = item.getMaintenancePlan() != null ? item.getMaintenancePlan().getMonthlyPrice() : BigDecimal.ZERO;

            itemDto.setUnitPrice(unitPrice);
            itemDto.setInstallationCharge(installCharge);
            itemDto.setMaintenanceCharge(maintCharge);

            BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal lineInstallation = installCharge.multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal lineMaintenance = maintCharge.multiply(BigDecimal.valueOf(item.getQuantity()));
            itemDto.setSubtotal(lineSubtotal.add(lineInstallation).add(lineMaintenance));

            subtotal = subtotal.add(lineSubtotal);
            totalInstallation = totalInstallation.add(lineInstallation);
            totalMaintenance = totalMaintenance.add(lineMaintenance);

            itemResponses.add(itemDto);
        }

        resp.setItems(itemResponses);
        resp.setSubtotal(subtotal);
        resp.setTotalInstallationCharges(totalInstallation);
        resp.setTotalMaintenanceCharges(totalMaintenance);

        BigDecimal taxableAmount = subtotal.add(totalInstallation).add(totalMaintenance);
        BigDecimal taxAmount = taxableAmount.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP); // 18% standard GST
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal grandTotal = taxableAmount.add(taxAmount).subtract(discountAmount);

        resp.setTaxAmount(taxAmount);
        resp.setDiscountAmount(discountAmount);
        resp.setGrandTotal(grandTotal);

        return resp;
    }
}
