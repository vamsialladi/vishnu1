package com.example.project.controller;

import com.example.project.config.RazorpayConfig;
import com.example.project.dto.PaymentDto;
import com.example.project.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final RazorpayConfig razorpayConfig;

    public PaymentController(PaymentService paymentService, RazorpayConfig razorpayConfig) {
        this.paymentService = paymentService;
        this.razorpayConfig = razorpayConfig;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentDto.Response> processPayment(@Valid @RequestBody PaymentDto.ProcessRequest request,
                                                              @RequestParam String username) {
        return ResponseEntity.ok(paymentService.initiateAndProcessPayment(request, username));
    }

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<PaymentDto.RazorpayOrderResponse> createRazorpayOrder(
            @Valid @RequestBody PaymentDto.RazorpayCreateOrderRequest request,
            @RequestParam String username) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(request, username));
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<PaymentDto.Response> verifyRazorpayPayment(
            @Valid @RequestBody PaymentDto.RazorpayVerifyRequest request,
            @RequestParam String username) {
        return ResponseEntity.ok(paymentService.verifyRazorpayPayment(request, username));
    }

    @PostMapping("/razorpay/webhook")
    public ResponseEntity<PaymentDto.Response> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        return ResponseEntity.ok(paymentService.handleRazorpayWebhook(payload, signature));
    }

    @GetMapping("/razorpay/config")
    public ResponseEntity<Map<String, String>> getRazorpayConfig() {
        return ResponseEntity.ok(Map.of(
                "keyId", razorpayConfig.getKeyId(),
                "currency", razorpayConfig.getCurrency()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto.Response> getPaymentById(@PathVariable Long id,
                                                              @RequestParam String username) {
        return ResponseEntity.ok(paymentService.getPaymentById(id, username));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentDto.Response>> getPaymentsForBooking(@PathVariable Long bookingId,
                                                                           @RequestParam String username) {
        return ResponseEntity.ok(paymentService.getPaymentsForBooking(bookingId, username));
    }

    @GetMapping("/invoice/booking/{bookingId}")
    public ResponseEntity<PaymentDto.InvoiceResponse> getInvoiceByBookingId(@PathVariable Long bookingId,
                                                                            @RequestParam String username) {
        return ResponseEntity.ok(paymentService.getInvoiceByBookingId(bookingId, username));
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<PaymentDto.InvoiceResponse> getInvoiceById(@PathVariable Long id,
                                                                     @RequestParam String username) {
        return ResponseEntity.ok(paymentService.getInvoiceById(id, username));
    }
}
