package com.api.appointment.controller;

import com.api.appointment.dto.AppointmentRejectDTO;
import com.api.appointment.dto.AppointmentRequestDTO;
import com.api.appointment.dto.AppointmentResponseDTO;
import com.api.appointment.dto.AppointmentStatusUpdateDTO;
import com.api.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

  private final AppointmentService appointmentService;

  public AppointmentController(AppointmentService appointmentService) {
    this.appointmentService = appointmentService;
  }

  // Get all appointments
  @GetMapping
  public List<AppointmentResponseDTO> getAllAppointments() {
    return appointmentService.getAllAppointments();
  }

  // Get appointment by ID
  @GetMapping("/{id}")
  public ResponseEntity<AppointmentResponseDTO> getAppointmentById(
    @PathVariable Integer id
  ) {
    return appointmentService
      .getAppointmentById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  // Patient creates appointment request
  @PostMapping
  public ResponseEntity<AppointmentResponseDTO> createAppointment(
    @Valid @RequestBody AppointmentRequestDTO request
  ) {
    return appointmentService.createAppointment(request);
  }

  // Nurse views pending appointments
  @GetMapping("/pending")
  public List<AppointmentResponseDTO> getPendingAppointments() {
    return appointmentService.getPendingAppointments();
  }

  // Nurse approves appointment
  @PatchMapping("/{id}/approve")
  public ResponseEntity<AppointmentResponseDTO> approveAppointment(
    @PathVariable Integer id
  ) {
    return appointmentService
      .approveAppointment(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  // Nurse rejects appointment
  @PatchMapping("/{id}/reject")
  public ResponseEntity<AppointmentResponseDTO> rejectAppointment(
    @PathVariable Integer id,
    @Valid @RequestBody AppointmentRejectDTO request
  ) {
    return appointmentService
      .rejectAppointment(id, request)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
}
