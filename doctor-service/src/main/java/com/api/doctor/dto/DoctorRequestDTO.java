package com.api.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequestDTO {

  @NotNull(message = "User ID is required")
  private Integer userId;

  @NotBlank(message = "Specialization is required")
  private String specialization;

  @NotBlank(message = "License number is required")
  private String licenseNumber;

  private String status;
}
