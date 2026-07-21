package com.api.schedule.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
  name = "user-service",
  url = "${services.user-service.url:http://user-service:8081}"
)
public interface UserClient {
  @GetMapping("/users/{id}")
  ResponseEntity<UserResponse> getUserById(@PathVariable("id") Integer id);

  class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private String role;
    private String phone;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String getPhone() {
      return phone;
    }

    public void setPhone(String phone) {
      this.phone = phone;
    }
  }
}
