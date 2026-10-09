package com.example.project.service;

import com.example.project.dto.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingDto.Response createBooking(BookingDto.CreateRequest request, String userEmail);
    BookingDto.Response getBookingById(Long id, String userEmail);
    Page<BookingDto.Response> getBookings(String userEmail, Pageable pageable);
    BookingDto.Response updateBookingStatus(Long id, BookingDto.StatusUpdateRequest request, String userEmail);
    BookingDto.Response cancelBooking(Long id, String userEmail);
}
