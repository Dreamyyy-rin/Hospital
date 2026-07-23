package com.api.appointment.client;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {
  @GetMapping("/payments/{id}")
  ResponseEntity<PaymentResponse> getPaymentById(
    @PathVariable("id") Integer id
  );

  @PostMapping("/payments")
  ResponseEntity<PaymentResponse> createPayment(
    @RequestBody PaymentRequest request
  );

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class PaymentResponse {

    private Integer id;
    private Integer appointmentId;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private String paymentMethod;
    private String status;
    private java.time.LocalDateTime createdAt;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  class PaymentRequest {

    private Integer appointmentId;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private String paymentMethod;
  }
}
