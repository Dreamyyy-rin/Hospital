package com.api.appointment.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "medical-record-service")
public interface MedicalRecordClient {
  @GetMapping("/medical-records/{id}")
  ResponseEntity<MedicalRecordResponse> getMedicalRecordById(
    @PathVariable("id") String id
  );

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class MedicalRecordResponse {

    private String id;
    private String appointmentId;
    private Integer doctorId;
    private Integer patientId;
  }
}
