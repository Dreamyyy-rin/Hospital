package com.api.appointment.client;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "medical-record-service")
public interface MedicalRecordClient {
  @GetMapping("/medical-records/{id}")
  ResponseEntity<MedicalRecordResponse> getMedicalRecordById(
    @PathVariable("id") String id
  );

  @PostMapping("/medical-records")
  ResponseEntity<MedicalRecordResponse> createMedicalRecord(
    @RequestBody MedicalRecordRequest request
  );

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class MedicalRecordResponse {

    private String id;
    private String appointmentId;
    private Integer doctorId;
    private Integer patientId;
    private List<String> complaints;
    private String diagnosis;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class MedicalRecordRequest {

    private String appointmentId;
    private Integer doctorId;
    private Integer patientId;
    private List<String> complaints;
  }
}
