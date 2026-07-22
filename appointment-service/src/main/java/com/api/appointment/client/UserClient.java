package com.api.appointment.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
  @GetMapping("/users/{id}")
  ResponseEntity<UserResponse> getUserById(@PathVariable("id") Integer id);

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String address;
  }
}
