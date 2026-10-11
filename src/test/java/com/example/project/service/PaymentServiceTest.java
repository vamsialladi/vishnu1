package com.example.project.service;

import com.example.project.config.RazorpayConfig;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private RazorpayConfig razorpayConfig;

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
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should successfully create Razorpay order for booking")
    void shouldCreateRazorpayOrder() {
        when(userRepository.findByEmail("cust@example.com")).thenReturn(Optional.of(customer));
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(razorpayConfig.getKeyId()).thenReturn("rzp_test_TmQBILcdURyzvk");
        when(razorpayConfig.getCurrency()).thenReturn("INR");
        when(paymentGateway.createOrder(any(), eq(BigDecimal.valueOf(2500)), eq("INR"), any()))
                .thenReturn("order_test_12345");

        PaymentDto.RazorpayCreateOrderRequest req = new PaymentDto.RazorpayCreateOrderRequest(10L);
        PaymentDto.RazorpayOrderResponse resp = paymentService.createRazorpayOrder(req, "cust@example.com");

        assertThat(resp).isNotNull();
        assertThat(resp.getOrderId()).isEqualTo("order_test_12345");
        assertThat(resp.getBookingId()).isEqualTo(10L);
        assertThat(resp.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(2500));
        assertThat(resp.getAmountInPaise()).isEqualTo(250000L);
        assertThat(resp.getKeyId()).isEqualTo("rzp_test_TmQBILcdURyzvk");
        assertThat(resp.getCurrency()).isEqualTo("INR");

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should verify valid Razorpay payment signature and confirm booking")
    void shouldVerifyValidRazorpaySignature() {
        when(userRepository.findByEmail("cust@example.com")).thenReturn(Optional.of(customer));
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(paymentGateway.verifySignature("order_test_12345", "pay_test_999", "sig_valid_123")).thenReturn(true);
        when(paymentRepository.findByTransactionId("pay_test_999")).thenReturn(Optional.empty());

        Payment savedPayment = new Payment();
        savedPayment.setId(200L);
        savedPayment.setBooking(booking);
        savedPayment.setTransactionId("pay_test_999");
        savedPayment.setGatewayReference("order_test_12345");
        savedPayment.setAmount(BigDecimal.valueOf(2500));
        savedPayment.setStatus("SUCCESS");
        savedPayment.setPaymentMethod("UPI");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PaymentDto.RazorpayVerifyRequest verifyReq = new PaymentDto.RazorpayVerifyRequest();
        verifyReq.setBookingId(10L);
        verifyReq.setRazorpayOrderId("order_test_12345");
        verifyReq.setRazorpayPaymentId("pay_test_999");
        verifyReq.setRazorpaySignature("sig_valid_123");
        verifyReq.setPaymentMethod("UPI");

        PaymentDto.Response resp = paymentService.verifyRazorpayPayment(verifyReq, "cust@example.com");

        assertThat(resp).isNotNull();
        assertThat(resp.getTransactionId()).isEqualTo("pay_test_999");
        assertThat(resp.getGatewayReference()).isEqualTo("order_test_12345");
        assertThat(resp.getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(booking.getPaymentStatus()).isEqualTo(Booking.PaymentStatus.PAID);
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should throw exception on invalid Razorpay payment signature")
    void shouldRejectInvalidRazorpaySignature() {
        when(userRepository.findByEmail("cust@example.com")).thenReturn(Optional.of(customer));
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
        when(paymentGateway.verifySignature("order_test_12345", "pay_test_999", "sig_invalid")).thenReturn(false);

        PaymentDto.RazorpayVerifyRequest verifyReq = new PaymentDto.RazorpayVerifyRequest();
        verifyReq.setBookingId(10L);
        verifyReq.setRazorpayOrderId("order_test_12345");
        verifyReq.setRazorpayPaymentId("pay_test_999");
        verifyReq.setRazorpaySignature("sig_invalid");

        assertThatThrownBy(() -> paymentService.verifyRazorpayPayment(verifyReq, "cust@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Razorpay payment signature");

        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
