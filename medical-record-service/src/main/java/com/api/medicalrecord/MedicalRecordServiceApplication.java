package com.api.medicalrecord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class MedicalRecordServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MedicalRecordServiceApplication.class, args);
  }
}
