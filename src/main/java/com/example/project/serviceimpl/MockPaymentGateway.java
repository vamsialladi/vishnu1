package com.example.project.serviceimpl;

import com.example.project.service.PaymentGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public String getGatewayName() {
        return "MOCK_GATEWAY";
    }

    @Override
    public PaymentGatewayResult processPayment(String transactionId, BigDecimal amount, String currency, String paymentMethod) {
        String reference = "MOCK-REF-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        return new PaymentGatewayResult(true, reference, null);
    }
}
