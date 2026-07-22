package com.api.medicalrecord.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponseDTO {

    private String id;
    private String appointmentId;
    private Integer doctorId;
    private Integer patientId;
    private List<String> complaints;
    private String diagnosis;
    private String bloodPressure;
    private Double temperature;
    private List<MedicationDTO> medications;
    private String doctorNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
