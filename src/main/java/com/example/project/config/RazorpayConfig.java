package com.example.project.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key-id:rzp_test_TmQBILcdURyzvk}")
    private String keyId;

    @Value("${razorpay.key-secret:OlhygDhyB511YDUwtc29JyH0}")
    private String keySecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Value("${razorpay.webhook-secret:}")
    private String webhookSecret;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }

    @Bean("razorpayKeyId")
    public String razorpayKeyId() {
        return keyId;
    }

    @Bean("razorpayKeySecret")
    public String razorpayKeySecret() {
        return keySecret;
    }

    @Bean("razorpayCurrency")
    public String razorpayCurrency() {
        return currency;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public String getCurrency() {
        return currency;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
}
