package com.example.project.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface PaymentGateway {
    String getGatewayName();
    PaymentGatewayResult processPayment(String transactionId, BigDecimal amount, String currency, String paymentMethod);

    default String createOrder(String receipt, BigDecimal amount, String currency, Map<String, String> notes) {
        return "ORDER-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    default boolean verifySignature(String orderId, String paymentId, String signature) {
        return true;
    }

    default boolean verifyWebhookSignature(String payload, String signature) {
        return true;
    }

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
