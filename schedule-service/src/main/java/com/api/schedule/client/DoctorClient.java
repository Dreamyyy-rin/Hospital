package com.api.schedule.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
  name = "doctor-service",
  url = "${services.doctor-service.url:http://doctor-service:8082}"
)
public interface DoctorClient {
  @GetMapping("/doctors/{id}")
  ResponseEntity<DoctorResponse> getDoctorById(@PathVariable("id") Integer id);

  class DoctorResponse {

    private Integer id;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String specialization;
    private String licenseNumber;
    private String status;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public Integer getUserId() {
      return userId;
    }

    public void setUserId(Integer userId) {
      this.userId = userId;
    }

    public String getUserName() {
      return userName;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }

    public String getUserEmail() {
      return userEmail;
    }

    public void setUserEmail(String userEmail) {
      this.userEmail = userEmail;
    }

    public String getSpecialization() {
      return specialization;
    }

    public void setSpecialization(String specialization) {
      this.specialization = specialization;
    }

    public String getLicenseNumber() {
      return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
      this.licenseNumber = licenseNumber;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }
  }
}
