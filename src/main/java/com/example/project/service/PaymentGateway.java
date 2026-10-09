package com.example.project.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    String getGatewayName();
    PaymentGatewayResult processPayment(String transactionId, BigDecimal amount, String currency, String paymentMethod);

    class PaymentGatewayResult {
        private final boolean success;
        private final String gatewayReference;
        private final String errorMessage;

        public PaymentGatewayResult(boolean success, String gatewayReference, String errorMessage) {
            this.success = success;
            this.gatewayReference = gatewayReference;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public String getGatewayReference() { return gatewayReference; }
        public String getErrorMessage() { return errorMessage; }
    }
}
