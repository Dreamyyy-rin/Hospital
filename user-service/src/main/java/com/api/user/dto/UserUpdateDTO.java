package com.api.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

  @Size(
    min = 2,
    max = 100,
    message = "Name must be between 2 and 100 characters"
  )
  private String name;

  private String phone;
}
