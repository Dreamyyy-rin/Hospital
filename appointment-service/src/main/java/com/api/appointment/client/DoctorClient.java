package com.api.appointment.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "doctor-service")
public interface DoctorClient {
  @GetMapping("/doctors/{id}")
  ResponseEntity<DoctorResponse> getDoctorById(@PathVariable("id") Integer id);

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class DoctorResponse {

    private Integer id;
    private Integer userId;
    private String specialization;
    private String licenseNumber;
    private String status;
    private String userName;
    private String userEmail;
  }
}
