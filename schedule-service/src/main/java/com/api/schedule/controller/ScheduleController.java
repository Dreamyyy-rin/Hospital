package com.api.schedule.controller;

import com.api.schedule.dto.ScheduleRequestDTO;
import com.api.schedule.dto.ScheduleResponseDTO;
import com.api.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleService scheduleService;

  public ScheduleController(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
  }

  // Get all schedules - ADMIN or DOCTOR
  @GetMapping
  @PreAuthorize("isAuthenticated()")
public ResponseEntity<List<ScheduleResponseDTO>> getAllSchedules() {
    List<ScheduleResponseDTO> schedules = scheduleService
      .getAllSchedules()
      .stream()
      .map(scheduleService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(schedules);
  }

  // Get schedule by ID - authenticated
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
public ResponseEntity<ScheduleResponseDTO> getScheduleById(
    @PathVariable Integer id
  ) {
    return scheduleService
      .getScheduleById(id)
      .map(schedule -> ResponseEntity.ok(scheduleService.toDTO(schedule)))
      .orElse(ResponseEntity.notFound().build());
  }

  // Get schedules by doctor - any authenticated
  @GetMapping("/doctor/{doctorId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ScheduleResponseDTO>> getSchedulesByDoctorId(
    @PathVariable Integer doctorId
  ) {
    List<ScheduleResponseDTO> schedules = scheduleService
      .getSchedulesByDoctorId(doctorId)
      .stream()
      .map(scheduleService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(schedules);
  }

  // Get available schedules - any authenticated (for patients)
  @GetMapping("/available")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ScheduleResponseDTO>> getAvailableSchedules() {
    List<ScheduleResponseDTO> schedules = scheduleService
      .getAvailableSchedules()
      .stream()
      .map(scheduleService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(schedules);
  }

  // Create schedule - ADMIN or DOCTOR
  @PostMapping
  @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
public ResponseEntity<ScheduleResponseDTO> createSchedule(
    @Valid @RequestBody ScheduleRequestDTO request
  ) {
    ScheduleResponseDTO created = scheduleService.createSchedule(request);
    return ResponseEntity.ok(created);
  }

  // Update schedule - ADMIN or DOCTOR (owner)
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
public ResponseEntity<ScheduleResponseDTO> updateSchedule(
    @PathVariable Integer id,
    @Valid @RequestBody ScheduleRequestDTO request
  ) {
    try {
      ScheduleResponseDTO updated = scheduleService.updateSchedule(id, request);
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // Update schedule status - ADMIN or DOCTOR
  @PatchMapping("/{id}/status")
  @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
  public ResponseEntity<ScheduleResponseDTO> updateStatus(
    @PathVariable Integer id,
    @RequestBody ScheduleRequestDTO request
  ) {
    try {
      ScheduleResponseDTO updated = scheduleService.updateStatus(
        id,
        request.getStatus()
      );
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // Delete schedule - ADMIN only
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
    scheduleService.deleteSchedule(id);
    return ResponseEntity.noContent().build();
  }
}
