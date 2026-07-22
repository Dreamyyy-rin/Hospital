package com.api.appointment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {

  @NotNull(message = "Patient ID is required")
  private Integer patientId;

  @NotNull(message = "Doctor ID is required")
  private Integer doctorId;

  @NotNull(message = "Schedule ID is required")
  private Integer scheduleId;

  private String complaint;
}
