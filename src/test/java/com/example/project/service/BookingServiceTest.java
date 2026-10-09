package com.example.project.service;

import com.example.project.dto.BookingDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyLocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private SiteInspectionRepository siteInspectionRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User testUser;
    private Company testCompany;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new User("client", "client@domain.com", "pass", "Client User", Role.ROLE_CUSTOMER);
        testUser.setId(1L);

        testCompany = new Company("My Plant Inc", "REG-888", "client@domain.com", "9876543210", testUser);
        testCompany.setId(10L);

        testProduct = new Product("Snake Plant", "Indoor plant", new BigDecimal("499.00"), 50, "Indoor", "SKU-SNK-01");
        testProduct.setId(100L);
        testProduct.setRentalPrice(new BigDecimal("99.00"));
        testProduct.setInstallationCharge(new BigDecimal("50.00"));

        testCart = new Cart(testUser);
        testCart.setId(5L);
        CartItem cartItem = new CartItem(testCart, testProduct, 2, false);
        testCart.setItems(new ArrayList<>(List.of(cartItem)));
    }

    @Test
    void testCreateBookingSuccess() {
        BookingDto.CreateRequest request = new BookingDto.CreateRequest();
        request.setCompanyId(10L);
        request.setNotes("Ground floor lobby installation");

        when(userRepository.findByEmail("client@domain.com")).thenReturn(Optional.of(testUser));
        when(companyRepository.findById(10L)).thenReturn(Optional.of(testCompany));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(inventoryService.reserveStock(100L, 2)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(200L);
            return b;
        });

        BookingDto.Response response = bookingService.createBooking(request, "client@domain.com");

        assertNotNull(response);
        assertEquals(200L, response.getId());
        assertEquals("My Plant Inc", response.getCompanyName());
        verify(inventoryService, times(1)).reserveStock(100L, 2);
    }

    @Test
    void testCancelCompletedBookingThrowsException() {
        Booking booking = new Booking("BK-999", testUser, testCompany, null, null);
        booking.setId(200L);
        booking.setStatus(Booking.BookingStatus.COMPLETED);

        when(userRepository.findByEmail("client@domain.com")).thenReturn(Optional.of(testUser));
        when(bookingRepository.findById(200L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () ->
                bookingService.cancelBooking(200L, "client@domain.com"));
    }

    @Test
    void testUnauthorizedUserCannotAccessBooking() {
        User otherUser = new User("other", "other@domain.com", "pass", "Other User", Role.ROLE_CUSTOMER);
        otherUser.setId(999L);

        Booking booking = new Booking("BK-999", testUser, testCompany, null, null);
        booking.setId(200L);

        when(userRepository.findByEmail("other@domain.com")).thenReturn(Optional.of(otherUser));
        when(bookingRepository.findById(200L)).thenReturn(Optional.of(booking));

        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
                bookingService.getBookingById(200L, "other@domain.com"));
    }
}
