package com.api.doctor.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserClient {
  @GetMapping("/users/{id}")
  ResponseEntity<UserResponse> getUserById(@PathVariable("id") Integer id);

  @PostMapping("/api/auth/register")
  ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request);

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private String role;
    private String phone;
  }
}
