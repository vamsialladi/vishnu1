package com.example.project.service;

import com.example.project.dto.PaymentDto;
import java.util.List;

public interface PaymentService {
    PaymentDto.Response initiateAndProcessPayment(PaymentDto.ProcessRequest request, String userEmail);
    PaymentDto.RazorpayOrderResponse createRazorpayOrder(PaymentDto.RazorpayCreateOrderRequest request, String userEmail);
    PaymentDto.Response verifyRazorpayPayment(PaymentDto.RazorpayVerifyRequest request, String userEmail);
    PaymentDto.Response handleRazorpayWebhook(String payload, String signature);
    PaymentDto.Response getPaymentById(Long id, String userEmail);
    List<PaymentDto.Response> getPaymentsForBooking(Long bookingId, String userEmail);
    PaymentDto.InvoiceResponse getInvoiceByBookingId(Long bookingId, String userEmail);
    PaymentDto.InvoiceResponse getInvoiceById(Long id, String userEmail);
}
