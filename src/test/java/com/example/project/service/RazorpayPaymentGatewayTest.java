package com.example.project.service;

import com.example.project.config.RazorpayConfig;
import com.example.project.serviceimpl.RazorpayPaymentGateway;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RazorpayPaymentGatewayTest {

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private RazorpayConfig razorpayConfig;

    private RazorpayPaymentGateway gateway;

    private final String secret = "OlhygDhyB511YDUwtc29JyH0";

    @BeforeEach
    void setUp() {
        gateway = new RazorpayPaymentGateway(razorpayClient, razorpayConfig);
    }

    private String calculateHmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(2 * rawHmac.length);
        for (byte b : rawHmac) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    @Test
    @DisplayName("Should return gateway name as RAZORPAY")
    void shouldReturnGatewayName() {
        assertThat(gateway.getGatewayName()).isEqualTo("RAZORPAY");
    }

    @Test
    @DisplayName("Should verify valid HMAC SHA256 payment signature")
    void shouldVerifyValidSignature() throws Exception {
        String orderId = "order_TmQnUfol0QqZGa";
        String paymentId = "pay_TmQp123456789";
        String payload = orderId + "|" + paymentId;
        String validSignature = calculateHmacSha256(payload, secret);

        when(razorpayConfig.getKeySecret()).thenReturn(secret);

        boolean isValid = gateway.verifySignature(orderId, paymentId, validSignature);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid signature")
    void shouldRejectInvalidSignature() {
        String orderId = "order_TmQnUfol0QqZGa";
        String paymentId = "pay_TmQp123456789";
        String invalidSignature = "invalid_signature_hex_value";

        when(razorpayConfig.getKeySecret()).thenReturn(secret);

        boolean isValid = gateway.verifySignature(orderId, paymentId, invalidSignature);
        assertThat(isValid).isFalse();
    }
}
