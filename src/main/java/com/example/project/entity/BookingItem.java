package com.example.project.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_items")
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType = "BUY";

    @Column(name = "is_rental", nullable = false)
    private boolean isRental = false;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "installation_charge", precision = 12, scale = 2)
    private BigDecimal installationCharge = BigDecimal.ZERO;

    @Column(name = "maintenance_charge", precision = 12, scale = 2)
    private BigDecimal maintenanceCharge = BigDecimal.ZERO;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    public BookingItem() {}

    public BookingItem(Booking booking, Product product, Integer quantity, BigDecimal unitPrice, BigDecimal installationCharge, BigDecimal maintenanceCharge, BigDecimal totalPrice, boolean isRental) {
        this.booking = booking;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.installationCharge = installationCharge;
        this.maintenanceCharge = maintenanceCharge;
        this.totalPrice = totalPrice;
        this.isRental = isRental;
        this.itemType = isRental ? "RENT" : "BUY";
    }

    public BookingItem(Booking booking, Product product, Integer quantity, String itemType, BigDecimal unitPrice, BigDecimal installationCharge, BigDecimal maintenanceCharge, BigDecimal totalPrice) {
        this.booking = booking;
        this.product = product;
        this.quantity = quantity;
        this.itemType = itemType;
        this.isRental = "RENT".equalsIgnoreCase(itemType);
        this.unitPrice = unitPrice;
        this.installationCharge = installationCharge;
        this.maintenanceCharge = maintenanceCharge;
        this.totalPrice = totalPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

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
        this.isRental = rental;
        this.itemType = rental ? "RENT" : "BUY";
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getInstallationCharge() { return installationCharge; }
    public void setInstallationCharge(BigDecimal installationCharge) { this.installationCharge = installationCharge; }

    public BigDecimal getMaintenanceCharge() { return maintenanceCharge; }
    public void setMaintenanceCharge(BigDecimal maintenanceCharge) { this.maintenanceCharge = maintenanceCharge; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}
