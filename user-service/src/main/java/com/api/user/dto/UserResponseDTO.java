package com.api.user.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

  private Integer id;
  private String name;
  private String email;
  private String role;
  private String phone;
  private LocalDateTime createdAt;
}
