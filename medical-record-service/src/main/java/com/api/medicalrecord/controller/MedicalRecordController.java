package com.api.medicalrecord.controller;

import com.api.medicalrecord.dto.MedicalRecordRequestDTO;
import com.api.medicalrecord.dto.MedicalRecordResponseDTO;
import com.api.medicalrecord.service.MedicalRecordService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // Get all medical records - ADMIN or DOCTOR only
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
public List<MedicalRecordResponseDTO> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    // Get medical record by ID - ADMIN or DOCTOR
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecordById(
            @PathVariable String id) {
        return medicalRecordService.getMedicalRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create medical record - ADMIN or DOCTOR (typically via appointment-service orchestration)
    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequestDTO request) {
        return ResponseEntity.ok(medicalRecordService.createMedicalRecord(request));
    }

    // Update medical record - ADMIN or DOCTOR (owner)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
public ResponseEntity<MedicalRecordResponseDTO> updateMedicalRecord(
            @PathVariable String id,
            @Valid @RequestBody MedicalRecordRequestDTO request) {
        return medicalRecordService.updateMedicalRecord(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete medical record - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable String id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}
