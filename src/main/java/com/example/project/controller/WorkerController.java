package com.example.project.controller;

import com.example.project.dto.WorkerDto;
import com.example.project.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping
    public ResponseEntity<WorkerDto.Response> registerWorker(@Valid @RequestBody WorkerDto.CreateRequest request) {
        return new ResponseEntity<>(workerService.registerWorker(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerDto.Response> getWorkerById(@PathVariable Long id) {
        return ResponseEntity.ok(workerService.getWorkerById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<WorkerDto.Response> getCurrentWorker(@RequestParam String username) {
        return ResponseEntity.ok(workerService.getWorkerByEmail(username));
    }

    @GetMapping
    public ResponseEntity<List<WorkerDto.Response>> getAllWorkers() {
        return ResponseEntity.ok(workerService.getAllWorkers());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkerDto.Response> updateWorkerStatus(@PathVariable Long id,
                                                                 @RequestBody WorkerDto.UpdateStatusRequest request) {
        return ResponseEntity.ok(workerService.updateWorkerStatus(id, request));
    }

    @PostMapping("/assign")
    public ResponseEntity<Void> assignWorker(@RequestParam Long bookingId,
                                             @RequestParam Long workerId,
                                             @RequestParam(required = false) String notes) {
        workerService.assignWorkerToBooking(bookingId, workerId, notes);
        return ResponseEntity.ok().build();
    }
}
