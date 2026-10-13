package com.example.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    public static class ProcessRequest {
        @NotNull(message = "Booking ID is required")
        private Long bookingId;

        @NotNull(message = "Payment method is required")
        private String paymentMethod; // CARD, UPI, NET_BANKING, INVOICE

        public ProcessRequest() {}

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class Response {
        private Long id;
        private String transactionId;
        private Long bookingId;
        private String bookingNumber;
        private BigDecimal amount;
        private String currency;
        private String paymentMethod;
        private String paymentStatus;
        private String gatewayReference;
        private LocalDateTime paymentDate;

        public Response() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getBookingNumber() { return bookingNumber; }
        public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getStatus() { return paymentStatus; }

        public String getGatewayReference() { return gatewayReference; }
        public void setGatewayReference(String gatewayReference) { this.gatewayReference = gatewayReference; }

        public LocalDateTime getPaymentDate() { return paymentDate; }
        public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    }

    public static class InvoiceResponse {
        private Long id;
        private String invoiceNumber;
        private Long bookingId;
        private String bookingNumber;
        private Long companyId;
        private String companyName;
        private BigDecimal plantCharges;
        private BigDecimal installationCharges;
        private BigDecimal maintenanceCharges;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private LocalDateTime invoiceDate;
        private String paymentStatus;

        public InvoiceResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getBookingNumber() { return bookingNumber; }
        public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public BigDecimal getPlantCharges() { return plantCharges; }
        public void setPlantCharges(BigDecimal plantCharges) { this.plantCharges = plantCharges; }

        public BigDecimal getInstallationCharges() { return installationCharges; }
        public void setInstallationCharges(BigDecimal installationCharges) { this.installationCharges = installationCharges; }

        public BigDecimal getMaintenanceCharges() { return maintenanceCharges; }
        public void setMaintenanceCharges(BigDecimal maintenanceCharges) { this.maintenanceCharges = maintenanceCharges; }

        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public LocalDateTime getInvoiceDate() { return invoiceDate; }
        public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    }

    public static class RazorpayCreateOrderRequest {
        @NotNull(message = "Booking ID is required")
        private Long bookingId;
        private String currency;

        public RazorpayCreateOrderRequest() {}
        public RazorpayCreateOrderRequest(Long bookingId) {
            this.bookingId = bookingId;
        }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    public static class RazorpayOrderResponse {
        private String orderId;
        private Long bookingId;
        private String bookingNumber;
        private BigDecimal amount;
        private Long amountInPaise;
        private String currency;
        private String keyId;
        private String receipt;
        private String companyName;
        private String customerName;
        private String customerEmail;
        private String customerPhone;

        public RazorpayOrderResponse() {}

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getBookingNumber() { return bookingNumber; }
        public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public Long getAmountInPaise() { return amountInPaise; }
        public void setAmountInPaise(Long amountInPaise) { this.amountInPaise = amountInPaise; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }

        public String getReceipt() { return receipt; }
        public void setReceipt(String receipt) { this.receipt = receipt; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    }

    public static class RazorpayVerifyRequest {
        @NotNull(message = "Booking ID is required")
        private Long bookingId;

        @NotBlank(message = "Razorpay order ID is required")
        private String razorpayOrderId;

        @NotBlank(message = "Razorpay payment ID is required")
        private String razorpayPaymentId;

        @NotBlank(message = "Razorpay signature is required")
        private String razorpaySignature;

        private String paymentMethod;

        public RazorpayVerifyRequest() {}

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

        public String getRazorpayOrderId() { return razorpayOrderId; }
        public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

        public String getRazorpayPaymentId() { return razorpayPaymentId; }
        public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

        public String getRazorpaySignature() { return razorpaySignature; }
        public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}
