package com.api.doctor.controller;

import com.api.doctor.dto.DoctorRequestDTO;
import com.api.doctor.dto.DoctorResponseDTO;
import com.api.doctor.dto.DoctorWithUserRequestDTO;
import com.api.doctor.dto.DoctorWithUserResponseDTO;
import com.api.doctor.service.DoctorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

  private final DoctorService doctorService;

  public DoctorController(DoctorService doctorService) {
    this.doctorService = doctorService;
  }

  // Get all doctors - ADMIN or DOCTOR
  @GetMapping
  @PreAuthorize("isAuthenticated()")
public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
    List<DoctorResponseDTO> doctors = doctorService
      .getAllDoctors()
      .stream()
      .map(doctorService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(doctors);
  }

  // Get doctor by ID - authenticated
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
public ResponseEntity<DoctorResponseDTO> getDoctorById(
    @PathVariable Integer id
  ) {
    return doctorService
      .getDoctorById(id)
      .map(doctor -> ResponseEntity.ok(doctorService.toDTO(doctor)))
      .orElse(ResponseEntity.notFound().build());
  }

  // Get doctor by user ID - authenticated
  @GetMapping("/user/{userId}")
  @PreAuthorize("isAuthenticated()")
public ResponseEntity<DoctorResponseDTO> getDoctorByUserId(
    @PathVariable Integer userId
  ) {
    return doctorService
      .getDoctorByUserId(userId)
      .map(doctor -> ResponseEntity.ok(doctorService.toDTO(doctor)))
      .orElse(ResponseEntity.notFound().build());
  }

  // Get active doctors - any authenticated user
  @GetMapping("/active")
  @PreAuthorize("isAuthenticated()")
public ResponseEntity<List<DoctorResponseDTO>> getActiveDoctors() {
    List<DoctorResponseDTO> doctors = doctorService
      .getActiveDoctors()
      .stream()
      .map(doctorService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(doctors);
  }

  // Create doctor - ADMIN only
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<DoctorResponseDTO> createDoctor(
    @Valid @RequestBody DoctorRequestDTO request
  ) {
    DoctorResponseDTO created = doctorService.createDoctor(request);
    return ResponseEntity.ok(created);
  }

  // Admin: Create both user account and doctor in one call
  @PostMapping("/admin/create-with-user")
  @PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<DoctorWithUserResponseDTO> createDoctorWithUser(
    @Valid @RequestBody DoctorWithUserRequestDTO request
  ) {
    DoctorWithUserResponseDTO created = doctorService.createDoctorWithUser(
      request
    );
    return ResponseEntity.ok(created);
  }

  // Update doctor - ADMIN only
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<DoctorResponseDTO> updateDoctor(
    @PathVariable Integer id,
    @Valid @RequestBody DoctorRequestDTO request
  ) {
    try {
      DoctorResponseDTO updated = doctorService.updateDoctor(id, request);
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // Delete doctor - ADMIN only
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteDoctor(@PathVariable Integer id) {
    doctorService.deleteDoctor(id);
    return ResponseEntity.noContent().build();
  }
}
