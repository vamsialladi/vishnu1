package com.example.project.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType = "BUY"; // "BUY" or "RENT"

    @Column(name = "is_rental", nullable = false)
    private boolean isRental = false;

    @Column(name = "include_installation", nullable = false)
    private boolean includeInstallation = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_plan_id")
    private MaintenancePlan maintenancePlan;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "installation_charge", precision = 12, scale = 2)
    private BigDecimal installationCharge = BigDecimal.ZERO;

    @Column(name = "maintenance_charge", precision = 12, scale = 2)
    private BigDecimal maintenanceCharge = BigDecimal.ZERO;

    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    public CartItem() {}

    public CartItem(Cart cart, Product product, Integer quantity, boolean isRental) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.isRental = isRental;
        this.itemType = isRental ? "RENT" : "BUY";
        this.unitPrice = isRental ? product.getRentalPrice() : product.getPrice();
        this.installationCharge = product.getInstallationCharge() != null ? product.getInstallationCharge() : BigDecimal.ZERO;
    }

    public CartItem(Cart cart, Product product, Integer quantity, String itemType, BigDecimal unitPrice, BigDecimal installationCharge, BigDecimal maintenanceCharge) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.itemType = itemType;
        this.isRental = "RENT".equalsIgnoreCase(itemType);
        this.unitPrice = unitPrice;
        this.installationCharge = installationCharge != null ? installationCharge : BigDecimal.ZERO;
        this.maintenanceCharge = maintenanceCharge != null ? maintenanceCharge : BigDecimal.ZERO;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity))
                .add(this.installationCharge.multiply(BigDecimal.valueOf(quantity)))
                .add(this.maintenanceCharge.multiply(BigDecimal.valueOf(quantity)));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) {
        this.itemType = itemType;
        this.isRental = "RENT".equalsIgnoreCase(itemType);
    }

    public boolean isRental() { return isRental; }
    public void setRental(boolean rental) {
        isRental = rental;
        this.itemType = rental ? "RENT" : "BUY";
    }

    public boolean isIncludeInstallation() { return includeInstallation; }
    public void setIncludeInstallation(boolean includeInstallation) { this.includeInstallation = includeInstallation; }

    public MaintenancePlan getMaintenancePlan() { return maintenancePlan; }
    public void setMaintenancePlan(MaintenancePlan maintenancePlan) { this.maintenancePlan = maintenancePlan; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getInstallationCharge() { return installationCharge; }
    public void setInstallationCharge(BigDecimal installationCharge) { this.installationCharge = installationCharge; }

    public BigDecimal getMaintenanceCharge() { return maintenanceCharge; }
    public void setMaintenanceCharge(BigDecimal maintenanceCharge) { this.maintenanceCharge = maintenanceCharge; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}
