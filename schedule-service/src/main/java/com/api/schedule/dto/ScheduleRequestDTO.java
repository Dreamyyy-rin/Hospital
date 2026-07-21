package com.api.schedule.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {

  @NotNull(message = "Doctor ID is required")
  private Integer doctorId;

  @NotNull(message = "Date is required")
  private LocalDate date;

  @NotNull(message = "Start time is required")
  private LocalTime startTime;

  @NotNull(message = "End time is required")
  private LocalTime endTime;

  private String room;

  private String status;
}
