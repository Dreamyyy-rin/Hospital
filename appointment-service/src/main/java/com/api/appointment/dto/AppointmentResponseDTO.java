package com.api.appointment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {

  private Integer id;
  private Integer patientId;
  private String patientName;
  private Integer doctorId;
  private String doctorName;
  private Integer scheduleId;
  private String appointmentDateTime;
  private String complaint;
  private String status;
  private String rejectionReason;
  private BigDecimal price;
  private Integer paymentId;
  private String paymentStatus;
  private String medicalRecordId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
