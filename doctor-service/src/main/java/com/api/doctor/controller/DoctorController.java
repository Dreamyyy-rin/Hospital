package com.api.doctor.controller;

import com.api.doctor.dto.DoctorRequestDTO;
import com.api.doctor.dto.DoctorResponseDTO;
import com.api.doctor.service.DoctorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

  private final DoctorService doctorService;

  public DoctorController(DoctorService doctorService) {
    this.doctorService = doctorService;
  }

  @GetMapping
  public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
    List<DoctorResponseDTO> doctors = doctorService
      .getAllDoctors()
      .stream()
      .map(doctorService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(doctors);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DoctorResponseDTO> getDoctorById(
    @PathVariable Integer id
  ) {
    return doctorService
      .getDoctorById(id)
      .map(doctor -> ResponseEntity.ok(doctorService.toDTO(doctor)))
      .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<DoctorResponseDTO> getDoctorByUserId(
    @PathVariable Integer userId
  ) {
    return doctorService
      .getDoctorByUserId(userId)
      .map(doctor -> ResponseEntity.ok(doctorService.toDTO(doctor)))
      .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/active")
  public ResponseEntity<List<DoctorResponseDTO>> getActiveDoctors() {
    List<DoctorResponseDTO> doctors = doctorService
      .getActiveDoctors()
      .stream()
      .map(doctorService::toDTO)
      .collect(Collectors.toList());
    return ResponseEntity.ok(doctors);
  }

  @PostMapping
  public ResponseEntity<DoctorResponseDTO> createDoctor(
    @Valid @RequestBody DoctorRequestDTO request
  ) {
    DoctorResponseDTO created = doctorService.createDoctor(request);
    return ResponseEntity.ok(created);
  }

  @PutMapping("/{id}")
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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDoctor(@PathVariable Integer id) {
    doctorService.deleteDoctor(id);
    return ResponseEntity.noContent().build();
  }
}
