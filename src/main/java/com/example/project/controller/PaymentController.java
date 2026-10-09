package com.example.project.controller;

import com.example.project.dto.PaymentDto;
import com.example.project.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentDto.Response> processPayment(@Valid @RequestBody PaymentDto.ProcessRequest request,
                                                              @RequestParam String username) {
        return ResponseEntity.ok(paymentService.initiateAndProcessPayment(request, username));
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
