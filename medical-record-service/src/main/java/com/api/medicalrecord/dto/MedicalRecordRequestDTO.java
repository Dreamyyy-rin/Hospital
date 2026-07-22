package com.api.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequestDTO {

  private String appointmentId;

  @NotNull(message = "Doctor ID is required")
  private Integer doctorId;

  @NotNull(message = "Patient ID is required")
  private Integer patientId;

  private List<String> complaints;
  private String diagnosis;
  private String bloodPressure;
  private Double temperature;
  private List<MedicationDTO> medications;
  private String doctorNotes;
}
