package com.example.project.service;

import com.example.project.dto.WorkerDto;
import com.example.project.entity.*;
import com.example.project.repository.*;
import com.example.project.serviceimpl.WorkerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceTest {

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private WorkerAssignmentRepository assignmentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private WorkerServiceImpl workerService;

    private User workerUser;
    private Worker worker;
    private Booking booking;

    @BeforeEach
    void setUp() {
        workerUser = new User("worker1", "worker1@example.com", "pass", "Worker One", Role.ROLE_WORKER);
        workerUser.setId(2L);

        worker = new Worker(workerUser, "EMP-101", "Indoor Horticulture");
        worker.setId(10L);

        booking = new Booking("BK-WRK", workerUser, null, null, null);
        booking.setId(20L);
    }

    @Test
    @DisplayName("Should register new worker")
    void shouldRegisterWorker() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(workerUser));
        when(workerRepository.existsByEmployeeCode("EMP-101")).thenReturn(false);
        when(workerRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(workerRepository.save(any(Worker.class))).thenReturn(worker);

        WorkerDto.CreateRequest request = new WorkerDto.CreateRequest();
        request.setUserId(2L);
        request.setEmployeeCode("EMP-101");
        request.setSpecialization("Indoor Horticulture");

        WorkerDto.Response response = workerService.registerWorker(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmployeeCode()).isEqualTo("EMP-101");
        assertThat(response.getSpecialization()).isEqualTo("Indoor Horticulture");
    }

    @Test
    @DisplayName("Should assign worker to booking and update booking status")
    void shouldAssignWorkerToBooking() {
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));
        when(workerRepository.findById(10L)).thenReturn(Optional.of(worker));

        workerService.assignWorkerToBooking(20L, 10L, "Lead technician assignment");

        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.WORKER_ASSIGNED);
        verify(assignmentRepository, times(1)).save(any(WorkerAssignment.class));
        verify(bookingRepository, times(1)).save(booking);
    }
}
