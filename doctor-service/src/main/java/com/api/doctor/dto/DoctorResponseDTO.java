package com.api.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDTO {

  private Integer id;
  private Integer userId;
  private String userName;
  private String userEmail;
  private String specialization;
  private String licenseNumber;
  private String status;
}
