package com.example.project.controller;

import com.example.project.dto.BookingDto;
import com.example.project.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto.Response> createBooking(@Valid @RequestBody BookingDto.CreateRequest request,
                                                             @RequestParam String username) {
        return new ResponseEntity<>(bookingService.createBooking(request, username), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto.Response> getBookingById(@PathVariable Long id,
                                                             @RequestParam String username) {
        return ResponseEntity.ok(bookingService.getBookingById(id, username));
    }

    @GetMapping
    public ResponseEntity<Page<BookingDto.Response>> getBookings(@RequestParam String username,
                                                                 @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookingService.getBookings(username, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingDto.Response> updateBookingStatus(@PathVariable Long id,
                                                                   @Valid @RequestBody BookingDto.StatusUpdateRequest request,
                                                                   @RequestParam String username) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, request, username));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDto.Response> cancelBooking(@PathVariable Long id,
                                                             @RequestParam String username) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, username));
    }
}
