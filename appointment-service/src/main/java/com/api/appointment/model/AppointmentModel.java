package com.api.appointment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "appointment")
@Data
public class AppointmentModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private Integer patientId;

  @Column(nullable = false)
  private Integer doctorId;

  @Column(nullable = false)
  private Integer scheduleId;

  @Column(columnDefinition = "TEXT")
  private String complaint;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private AppointmentStatus status;

  @Column(columnDefinition = "TEXT")
  private String rejectionReason;

  @Column(nullable = false)
  private LocalDateTime appointmentDate;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal price;

  private Integer paymentId;

  private String medicalRecordId;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public enum AppointmentStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    PAID,
    COMPLETED,
    CANCELLED,
  }
}
