package com.api.appointment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "appointment")
@Data
public class AppointmentConfig {

  private Integer price = 150000;
  private String currency = "IDR";
}
