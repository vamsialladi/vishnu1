package com.example.project.serviceimpl;

import com.example.project.config.RazorpayConfig;
import com.example.project.dto.PaymentDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.*;
import com.example.project.service.PaymentGateway;
import com.example.project.service.PaymentService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentGateway paymentGateway;
    private final RazorpayConfig razorpayConfig;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InvoiceRepository invoiceRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository,
                              PaymentGateway paymentGateway,
                              RazorpayConfig razorpayConfig) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.paymentGateway = paymentGateway;
        this.razorpayConfig = razorpayConfig;
    }

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InvoiceRepository invoiceRepository,
                              BookingRepository bookingRepository,
                              UserRepository userRepository,
                              PaymentGateway paymentGateway) {
        this(paymentRepository, invoiceRepository, bookingRepository, userRepository, paymentGateway, new RazorpayConfig());
    }

    private User getUser(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));
    }

    private void validateBookingAccess(Booking booking, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) return;
        if (!booking.getUser().getId().equals(user.getId()) &&
            !booking.getCompany().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied: You cannot view/pay for another user's booking.");
        }
    }

    @Override
    public PaymentDto.Response initiateAndProcessPayment(PaymentDto.ProcessRequest request, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.getBookingId()));

        validateBookingAccess(booking, user);

        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionId(txId);
        payment.setAmount(booking.getGrandTotal());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("INITIATED");

        // Process through gateway
        PaymentGateway.PaymentGatewayResult result = paymentGateway.processPayment(
                txId, booking.getGrandTotal(), "INR", request.getPaymentMethod());

        if (result.isSuccess()) {
            payment.setStatus("SUCCESS");
            payment.setGatewayReference(result.getGatewayReference());

            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            if (booking.getStatus() == Booking.BookingStatus.PENDING_PAYMENT) {
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
            }
            bookingRepository.save(booking);

            // Generate Invoice
            generateInvoiceForBooking(booking);
        } else {
            payment.setStatus("FAILED");
        }

        Payment saved = paymentRepository.save(payment);
        return mapToPaymentResponse(saved);
    }

    @Override
    public PaymentDto.RazorpayOrderResponse createRazorpayOrder(PaymentDto.RazorpayCreateOrderRequest request, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.getBookingId()));

        validateBookingAccess(booking, user);

        if (booking.getPaymentStatus() == Booking.PaymentStatus.PAID) {
            throw new IllegalStateException("Booking has already been paid for.");
        }

        String currency = (request.getCurrency() != null && !request.getCurrency().isBlank())
                ? request.getCurrency()
                : (razorpayConfig != null && razorpayConfig.getCurrency() != null ? razorpayConfig.getCurrency() : "INR");

        String receipt = "RCPT-" + booking.getId() + "-" + (System.currentTimeMillis() % 100000000);

        Map<String, String> notes = Map.of(
                "bookingId", String.valueOf(booking.getId()),
                "bookingNumber", booking.getBookingNumber() != null ? booking.getBookingNumber() : "",
                "customerEmail", user.getEmail() != null ? user.getEmail() : ""
        );

        String orderId = paymentGateway.createOrder(receipt, booking.getGrandTotal(), currency, notes);

        // Record pending payment entry
        Payment payment = paymentRepository.findByGatewayReference(orderId)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setBooking(booking);
                    p.setTransactionId("PENDING-" + orderId);
                    p.setGatewayReference(orderId);
                    p.setAmount(booking.getGrandTotal());
                    p.setPaymentMethod("RAZORPAY");
                    p.setStatus("INITIATED");
                    return p;
                });
        paymentRepository.save(payment);

        PaymentDto.RazorpayOrderResponse resp = new PaymentDto.RazorpayOrderResponse();
        resp.setOrderId(orderId);
        resp.setBookingId(booking.getId());
        resp.setBookingNumber(booking.getBookingNumber());
        resp.setAmount(booking.getGrandTotal());
        resp.setAmountInPaise(booking.getGrandTotal().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValue());
        resp.setCurrency(currency);
        resp.setKeyId(razorpayConfig != null && razorpayConfig.getKeyId() != null ? razorpayConfig.getKeyId() : "rzp_test_TmQBILcdURyzvk");
        resp.setReceipt(receipt);
        resp.setCompanyName(booking.getCompany() != null ? booking.getCompany().getName() : "PlantCare Platform");
        resp.setCustomerName(user.getFullName() != null ? user.getFullName() : user.getUsername());
        resp.setCustomerEmail(user.getEmail());
        resp.setCustomerPhone(user.getPhoneNumber());

        log.info("Created Razorpay order: orderId={}, bookingId={}, amount={}", orderId, booking.getId(), booking.getGrandTotal());
        return resp;
    }

    @Override
    public PaymentDto.Response verifyRazorpayPayment(PaymentDto.RazorpayVerifyRequest request, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.getBookingId()));

        validateBookingAccess(booking, user);

        boolean isValid = paymentGateway.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            log.warn("Invalid Razorpay payment signature for orderId={}, paymentId={}",
                    request.getRazorpayOrderId(), request.getRazorpayPaymentId());
            throw new IllegalArgumentException("Invalid Razorpay payment signature. Payment verification failed.");
        }

        // Idempotency check: if payment already recorded with this payment ID
        Optional<Payment> existing = paymentRepository.findByTransactionId(request.getRazorpayPaymentId());
        if (existing.isPresent() && "SUCCESS".equals(existing.get().getStatus())) {
            log.info("Payment already successfully verified for paymentId={}", request.getRazorpayPaymentId());
            return mapToPaymentResponse(existing.get());
        }

        // Find initialized payment or create new
        Payment payment = paymentRepository.findByGatewayReference(request.getRazorpayOrderId())
                .orElseGet(() -> paymentRepository.findByTransactionId("PENDING-" + request.getRazorpayOrderId())
                        .orElseGet(() -> {
                            Payment p = new Payment();
                            p.setBooking(booking);
                            return p;
                        }));

        payment.setTransactionId(request.getRazorpayPaymentId());
        payment.setGatewayReference(request.getRazorpayOrderId());
        payment.setAmount(booking.getGrandTotal());
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "RAZORPAY");
        payment.setStatus("SUCCESS");

        Payment saved = paymentRepository.save(payment);

        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        if (booking.getStatus() == Booking.BookingStatus.PENDING_PAYMENT) {
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
        }
        bookingRepository.save(booking);

        generateInvoiceForBooking(booking);

        log.info("Successfully verified Razorpay payment: paymentId={}, orderId={}, bookingId={}",
                request.getRazorpayPaymentId(), request.getRazorpayOrderId(), booking.getId());

        return mapToPaymentResponse(saved);
    }

    @Override
    public PaymentDto.Response handleRazorpayWebhook(String payload, String signature) {
        if (signature != null && !signature.isBlank()) {
            boolean valid = paymentGateway.verifyWebhookSignature(payload, signature);
            if (!valid) {
                log.warn("Invalid Razorpay webhook signature.");
                throw new IllegalArgumentException("Invalid Razorpay webhook signature.");
            }
        }

        JSONObject json = new JSONObject(payload);
        String event = json.optString("event");
        log.info("Handling Razorpay webhook event: {}", event);

        if ("payment.captured".equals(event) || "order.paid".equals(event)) {
            JSONObject payloadObj = json.optJSONObject("payload");
            if (payloadObj != null) {
                JSONObject paymentObj = payloadObj.optJSONObject("payment");
                if (paymentObj != null) {
                    JSONObject entity = paymentObj.optJSONObject("entity");
                    if (entity != null) {
                        String paymentId = entity.optString("id");
                        String orderId = entity.optString("order_id");
                        JSONObject notes = entity.optJSONObject("notes");
                        Long bookingId = null;
                        if (notes != null && notes.has("bookingId")) {
                            bookingId = notes.optLong("bookingId");
                        }

                        if (bookingId != null) {
                            Booking booking = bookingRepository.findById(bookingId).orElse(null);
                            if (booking != null) {
                                Payment payment = paymentRepository.findByTransactionId(paymentId)
                                        .orElseGet(() -> paymentRepository.findByGatewayReference(orderId)
                                                .orElseGet(() -> {
                                                    Payment p = new Payment();
                                                    p.setBooking(booking);
                                                    p.setTransactionId(paymentId);
                                                    p.setGatewayReference(orderId);
                                                    p.setAmount(booking.getGrandTotal());
                                                    p.setPaymentMethod("RAZORPAY_WEBHOOK");
                                                    return p;
                                                }));

                                payment.setTransactionId(paymentId);
                                payment.setStatus("SUCCESS");
                                Payment saved = paymentRepository.save(payment);

                                booking.setPaymentStatus(Booking.PaymentStatus.PAID);
                                if (booking.getStatus() == Booking.BookingStatus.PENDING_PAYMENT) {
                                    booking.setStatus(Booking.BookingStatus.CONFIRMED);
                                }
                                bookingRepository.save(booking);

                                generateInvoiceForBooking(booking);
                                return mapToPaymentResponse(saved);
                            }
                        }
                    }
                }
            }
        }

        PaymentDto.Response generic = new PaymentDto.Response();
        generic.setPaymentStatus("EVENT_PROCESSED");
        return generic;
    }

    private Invoice generateInvoiceForBooking(Booking booking) {
        return invoiceRepository.findByBookingId(booking.getId())
                .orElseGet(() -> {
                    Invoice inv = new Invoice();
                    inv.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    inv.setBooking(booking);
                    inv.setCompany(booking.getCompany());
                    inv.setPlantCharges(booking.getPlantCharges());
                    inv.setInstallationCharges(booking.getInstallationCharges());
                    inv.setMaintenanceCharges(booking.getMaintenanceCharges());
                    inv.setTaxAmount(booking.getTaxAmount());
                    inv.setDiscountAmount(booking.getDiscountAmount());
                    inv.setTotalAmount(booking.getGrandTotal());
                    inv.setPaymentStatus("PAID");
                    inv.setInvoiceDate(LocalDateTime.now());
                    return invoiceRepository.save(inv);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto.Response getPaymentById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        validateBookingAccess(payment.getBooking(), user);
        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.Response> getPaymentsForBooking(Long bookingId, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        validateBookingAccess(booking, user);

        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto.InvoiceResponse getInvoiceByBookingId(Long bookingId, String userEmail) {
        User user = getUser(userEmail);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
        validateBookingAccess(booking, user);

        Invoice invoice = invoiceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for booking: " + bookingId));
        return mapToInvoiceResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto.InvoiceResponse getInvoiceById(Long id, String userEmail) {
        User user = getUser(userEmail);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
        validateBookingAccess(invoice.getBooking(), user);
        return mapToInvoiceResponse(invoice);
    }

    private PaymentDto.Response mapToPaymentResponse(Payment payment) {
        PaymentDto.Response resp = new PaymentDto.Response();
        resp.setId(payment.getId());
        resp.setTransactionId(payment.getTransactionId());
        resp.setBookingId(payment.getBooking().getId());
        resp.setBookingNumber(payment.getBooking().getBookingNumber());
        resp.setAmount(payment.getAmount());
        resp.setCurrency("INR");
        resp.setPaymentMethod(payment.getPaymentMethod());
        resp.setPaymentStatus(payment.getStatus());
        resp.setGatewayReference(payment.getGatewayReference());
        resp.setPaymentDate(payment.getCreatedAt());
        return resp;
    }

    private PaymentDto.InvoiceResponse mapToInvoiceResponse(Invoice inv) {
        PaymentDto.InvoiceResponse resp = new PaymentDto.InvoiceResponse();
        resp.setId(inv.getId());
        resp.setInvoiceNumber(inv.getInvoiceNumber());
        resp.setBookingId(inv.getBooking().getId());
        resp.setBookingNumber(inv.getBooking().getBookingNumber());
        resp.setCompanyId(inv.getCompany() != null ? inv.getCompany().getId() : null);
        resp.setCompanyName(inv.getCompany() != null ? inv.getCompany().getName() : null);
        resp.setPlantCharges(inv.getPlantCharges());
        resp.setInstallationCharges(inv.getInstallationCharges());
        resp.setMaintenanceCharges(inv.getMaintenanceCharges());
        resp.setTaxAmount(inv.getTaxAmount());
        resp.setDiscountAmount(inv.getDiscountAmount());
        resp.setTotalAmount(inv.getTotalAmount());
        resp.setInvoiceDate(inv.getInvoiceDate());
        resp.setPaymentStatus(inv.getPaymentStatus());
        return resp;
    }
}
