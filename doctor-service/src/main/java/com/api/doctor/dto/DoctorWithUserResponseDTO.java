package com.api.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorWithUserResponseDTO {

  private UserDTO user;
  private DoctorResponseDTO doctor;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserDTO {

    private Integer id;
    private String name;
    private String email;
    private String role;
    private String phone;
  }
}
