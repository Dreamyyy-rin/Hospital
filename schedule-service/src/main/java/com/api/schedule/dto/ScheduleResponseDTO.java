package com.api.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDTO {

  private Integer id;
  private Integer doctorId;
  private String doctorName;
  private String doctorSpecialization;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime endTime;
  private String room;
  private String status;
}
