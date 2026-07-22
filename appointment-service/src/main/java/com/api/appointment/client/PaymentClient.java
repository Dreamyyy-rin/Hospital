package com.api.appointment.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-service")
public interface PaymentClient {
  @GetMapping("/payments/{id}")
  ResponseEntity<PaymentResponse> getPaymentById(
    @PathVariable("id") Integer id
  );

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class PaymentResponse {

    private Integer id;
    private Integer appointmentId;
    private java.math.BigDecimal totalAmount;
    private java.math.BigDecimal amountPaid;
    private java.math.BigDecimal changeAmount;
    private String paymentMethod;
    private String status;
    private java.time.LocalDateTime createdAt;
  }
}
