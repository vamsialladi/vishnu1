package com.example.project.service;

import com.example.project.dto.WorkerDto;
import java.util.List;

public interface WorkerService {
    WorkerDto.Response registerWorker(WorkerDto.CreateRequest request);
    WorkerDto.Response getWorkerById(Long id);
    WorkerDto.Response getWorkerByUserId(Long userId);
    WorkerDto.Response getWorkerByEmail(String email);
    List<WorkerDto.Response> getAllWorkers();
    WorkerDto.Response updateWorkerStatus(Long id, WorkerDto.UpdateStatusRequest request);
    void assignWorkerToBooking(Long bookingId, Long workerId, String notes);
}
