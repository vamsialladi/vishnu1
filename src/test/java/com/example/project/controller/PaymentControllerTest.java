package com.example.project.controller;

import com.example.project.config.RazorpayConfig;
import com.example.project.dto.PaymentDto;
import com.example.project.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private RazorpayConfig razorpayConfig;

    @Test
    @DisplayName("GET /api/payments/razorpay/config should return keyId and currency")
    void shouldReturnRazorpayConfig() throws Exception {
        mockMvc.perform(get("/api/payments/razorpay/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keyId").value("rzp_test_TmQBILcdURyzvk"))
                .andExpect(jsonPath("$.currency").value("INR"));
    }

    @Test
    @DisplayName("POST /api/payments/razorpay/create-order should return order details")
    void shouldCreateRazorpayOrder() throws Exception {
        PaymentDto.RazorpayOrderResponse orderResp = new PaymentDto.RazorpayOrderResponse();
        orderResp.setOrderId("order_test_99999");
        orderResp.setBookingId(10L);
        orderResp.setAmount(BigDecimal.valueOf(1500));
        orderResp.setAmountInPaise(150000L);
        orderResp.setCurrency("INR");
        orderResp.setKeyId("rzp_test_TmQBILcdURyzvk");

        when(paymentService.createRazorpayOrder(any(PaymentDto.RazorpayCreateOrderRequest.class), eq("cust@example.com")))
                .thenReturn(orderResp);

        PaymentDto.RazorpayCreateOrderRequest req = new PaymentDto.RazorpayCreateOrderRequest(10L);

        mockMvc.perform(post("/api/payments/razorpay/create-order")
                        .param("username", "cust@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order_test_99999"))
                .andExpect(jsonPath("$.bookingId").value(10))
                .andExpect(jsonPath("$.amount").value(1500))
                .andExpect(jsonPath("$.amountInPaise").value(150000))
                .andExpect(jsonPath("$.keyId").value("rzp_test_TmQBILcdURyzvk"));
    }

    @Test
    @DisplayName("POST /api/payments/razorpay/verify should verify payment and return response")
    void shouldVerifyRazorpayPayment() throws Exception {
        PaymentDto.Response paymentResp = new PaymentDto.Response();
        paymentResp.setId(50L);
        paymentResp.setBookingId(10L);
        paymentResp.setTransactionId("pay_test_888");
        paymentResp.setGatewayReference("order_test_99999");
        paymentResp.setPaymentStatus("SUCCESS");
        paymentResp.setAmount(BigDecimal.valueOf(1500));

        when(paymentService.verifyRazorpayPayment(any(PaymentDto.RazorpayVerifyRequest.class), eq("cust@example.com")))
                .thenReturn(paymentResp);

        PaymentDto.RazorpayVerifyRequest verifyReq = new PaymentDto.RazorpayVerifyRequest();
        verifyReq.setBookingId(10L);
        verifyReq.setRazorpayOrderId("order_test_99999");
        verifyReq.setRazorpayPaymentId("pay_test_888");
        verifyReq.setRazorpaySignature("sig_dummy_123");

        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .param("username", "cust@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("pay_test_888"))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(1500));
    }
}
