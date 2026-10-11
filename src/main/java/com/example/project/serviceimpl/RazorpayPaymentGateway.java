package com.example.project.serviceimpl;

import com.example.project.config.RazorpayConfig;
import com.example.project.service.PaymentGateway;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
@Primary
public class RazorpayPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentGateway.class);

    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    public RazorpayPaymentGateway(RazorpayClient razorpayClient, RazorpayConfig razorpayConfig) {
        this.razorpayClient = razorpayClient;
        this.razorpayConfig = razorpayConfig;
    }

    @Override
    public String getGatewayName() {
        return "RAZORPAY";
    }

    @Override
    public PaymentGatewayResult processPayment(String transactionId, BigDecimal amount, String currency, String paymentMethod) {
        try {
            String curr = (currency != null && !currency.isBlank()) ? currency : razorpayConfig.getCurrency();
            String orderId = createOrder("rcpt_" + transactionId, amount, curr, Map.of("transactionId", transactionId));
            return new PaymentGatewayResult(true, orderId, null);
        } catch (Exception e) {
            log.error("Failed to process payment with Razorpay: {}", e.getMessage(), e);
            return new PaymentGatewayResult(false, null, e.getMessage());
        }
    }

    @Override
    public String createOrder(String receipt, BigDecimal amount, String currency, Map<String, String> notes) {
        try {
            JSONObject orderRequest = new JSONObject();
            long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValue();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", (currency != null && !currency.isBlank()) ? currency : razorpayConfig.getCurrency());
            orderRequest.put("receipt", receipt);

            if (notes != null && !notes.isEmpty()) {
                JSONObject notesObj = new JSONObject();
                notes.forEach(notesObj::put);
                orderRequest.put("notes", notesObj);
            }

            log.info("Creating Razorpay order for receipt={}, amountInPaise={}", receipt, amountInPaise);
            Order order = razorpayClient.orders.create(orderRequest);
            return order.get("id");
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);
            return Utils.verifyPaymentSignature(attributes, razorpayConfig.getKeySecret());
        } catch (RazorpayException e) {
            log.warn("Razorpay signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            String secret = razorpayConfig.getWebhookSecret();
            if (secret == null || secret.isBlank()) {
                secret = razorpayConfig.getKeySecret();
            }
            return Utils.verifyWebhookSignature(payload, signature, secret);
        } catch (RazorpayException e) {
            log.warn("Razorpay webhook signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    public com.razorpay.Payment fetchPayment(String paymentId) throws RazorpayException {
        return razorpayClient.payments.fetch(paymentId);
    }

    public RazorpayConfig getRazorpayConfig() {
        return razorpayConfig;
    }
}
