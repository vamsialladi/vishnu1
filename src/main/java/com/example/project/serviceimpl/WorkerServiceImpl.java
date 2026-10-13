package com.example.project.serviceimpl;

import com.example.project.dto.WorkerDto;
import com.example.project.entity.*;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.repository.BookingRepository;
import com.example.project.repository.UserRepository;
import com.example.project.repository.WorkerAssignmentRepository;
import com.example.project.repository.WorkerRepository;
import com.example.project.service.WorkerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final WorkerAssignmentRepository assignmentRepository;

    public WorkerServiceImpl(WorkerRepository workerRepository,
                             UserRepository userRepository,
                             BookingRepository bookingRepository,
                             WorkerAssignmentRepository assignmentRepository) {
        this.workerRepository = workerRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public WorkerDto.Response registerWorker(WorkerDto.CreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        user.setRole(Role.ROLE_WORKER);
        userRepository.save(user);

        Worker worker = new Worker();
        worker.setUser(user);
        worker.setEmployeeCode(request.getEmployeeCode());
        worker.setSpecialization(request.getSpecialization());
        worker.setStatus("ACTIVE");
        worker.setAvailable(true);

        Worker saved = workerRepository.save(worker);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerDto.Response getWorkerById(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + id));
        return mapToResponse(worker);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerDto.Response getWorkerByUserId(Long userId) {
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker profile not found for user id: " + userId));
        return mapToResponse(worker);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerDto.Response getWorkerByEmail(String email) {
        User user = userRepository.findByEmail(email).or(() -> userRepository.findByUsername(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return getWorkerByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerDto.Response> getAllWorkers() {
        return workerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkerDto.Response updateWorkerStatus(Long id, WorkerDto.UpdateStatusRequest request) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + id));

        if (request.getStatus() != null) {
            worker.setStatus(request.getStatus().toUpperCase());
        }
        if (request.getIsAvailable() != null) {
            worker.setAvailable(request.getIsAvailable());
        }

        Worker saved = workerRepository.save(worker);
        return mapToResponse(saved);
    }

    @Override
    public void assignWorkerToBooking(Long bookingId, Long workerId, String notes) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + workerId));

        WorkerAssignment assignment = new WorkerAssignment();
        assignment.setBooking(booking);
        assignment.setWorker(worker);
        assignment.setNotes(notes);
        assignment.setStatus("ASSIGNED");
        assignment.setAssignmentType("INSTALLATION");
        assignment.setScheduledTime(LocalDateTime.now());

        assignmentRepository.save(assignment);

        booking.setStatus(Booking.BookingStatus.WORKER_ASSIGNED);
        bookingRepository.save(booking);
    }

    private WorkerDto.Response mapToResponse(Worker worker) {
        WorkerDto.Response resp = new WorkerDto.Response();
        resp.setId(worker.getId());
        resp.setUserId(worker.getUser().getId());
        resp.setFullName(worker.getUser().getFullName());
        resp.setEmail(worker.getUser().getEmail());
        resp.setEmployeeCode(worker.getEmployeeCode());
        resp.setSpecialization(worker.getSpecialization());
        resp.setPhone(worker.getUser().getPhone());
        resp.setStatus(worker.getStatus());
        resp.setAvailable(worker.isAvailable());
        return resp;
    }
}
