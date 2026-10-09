package com.example.project.service;

import com.example.project.dto.PaymentDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User customer;
    private Booking booking;

    @BeforeEach
    void setUp() {
        customer = new User("cust", "cust@example.com", "pass", "Customer One", Role.ROLE_CUSTOMER);
        customer.setId(1L);

        booking = new Booking();
        booking.setId(10L);
        booking.setBookingNumber("BK-12345");
        booking.setUser(customer);
        booking.setGrandTotal(BigDecimal.valueOf(2500));
        booking.setStatus(Booking.BookingStatus.PENDING_PAYMENT);
    }

    @Test
    @DisplayName("Should successfully initiate and process payment with gateway")
    void shouldProcessPayment() {
        when(userRepository.findByEmail("cust@example.com")).thenReturn(Optional.of(customer));
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        PaymentGateway.PaymentGatewayResult gatewayResult = new PaymentGateway.PaymentGatewayResult(
                true, "TXN-9999", null);
        when(paymentGateway.processPayment(any(), any(), any(), any())).thenReturn(gatewayResult);

        Payment payment = new Payment();
        payment.setId(100L);
        payment.setBooking(booking);
        payment.setAmount(BigDecimal.valueOf(2500));
        payment.setStatus("SUCCESS");
        payment.setTransactionId("TXN-9999");
        payment.setPaymentMethod("CARD");
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentDto.ProcessRequest request = new PaymentDto.ProcessRequest();
        request.setBookingId(10L);
        request.setPaymentMethod("CARD");

        PaymentDto.Response response = paymentService.initiateAndProcessPayment(request, "cust@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getTransactionId()).isEqualTo("TXN-9999");
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.PAYMENT_COMPLETED);
    }
}
