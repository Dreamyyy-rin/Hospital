package com.api.schedule.controller;

import com.api.schedule.dto.ScheduleRequestDTO;
import com.api.schedule.dto.ScheduleResponseDTO;
import com.api.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleService scheduleService;

  public ScheduleController(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
  }

  @GetMapping
  public ResponseEntity<List<ScheduleResponseDTO>> getAllSchedules() {
    List<ScheduleResponseDTO> schedules = scheduleService
      .getAllSchedules()
      .stream()
      .map(scheduleService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(schedules);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ScheduleResponseDTO> getScheduleById(
    @PathVariable Integer id
  ) {
    return scheduleService
      .getScheduleById(id)
      .map(schedule -> ResponseEntity.ok(scheduleService.toDTO(schedule)))
      .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/doctor/{doctorId}")
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

  @GetMapping("/available")
  public ResponseEntity<List<ScheduleResponseDTO>> getAvailableSchedules() {
    List<ScheduleResponseDTO> schedules = scheduleService
      .getAvailableSchedules()
      .stream()
      .map(scheduleService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(schedules);
  }

  @PostMapping
  public ResponseEntity<ScheduleResponseDTO> createSchedule(
    @Valid @RequestBody ScheduleRequestDTO request
  ) {
    ScheduleResponseDTO created = scheduleService.createSchedule(request);
    return ResponseEntity.ok(created);
  }

  @PutMapping("/{id}")
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

  @PatchMapping("/{id}/status")
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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
    scheduleService.deleteSchedule(id);
    return ResponseEntity.noContent().build();
  }
}
