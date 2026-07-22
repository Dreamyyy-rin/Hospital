package com.api.appointment.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "schedule-service")
public interface ScheduleClient {
  @GetMapping("/schedules/{id}")
  ResponseEntity<ScheduleResponse> getScheduleById(
    @PathVariable("id") Integer id
  );

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class ScheduleResponse {

    private Integer id;
    private Integer doctorId;
    private String date;
    private String startTime;
    private String endTime;
    private String room;
    private String status;
    private String doctorName;
  }
}
