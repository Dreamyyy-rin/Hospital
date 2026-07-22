package com.api.payment.controller;

import com.api.payment.dto.PaymentRequestDTO;
import com.api.payment.dto.PaymentResponseDTO;
import com.api.payment.dto.PaymentStatusUpdateRequestDTO;
import com.api.payment.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @GetMapping
  public List<PaymentResponseDTO> getAllPayments() {
    return paymentService.getAllPayments();
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponseDTO> getPaymentById(
    @PathVariable Integer id
  ) {
    return paymentService
      .getPaymentById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<PaymentResponseDTO> createPayment(
    @Valid @RequestBody PaymentRequestDTO request
  ) {
    return ResponseEntity.ok(paymentService.createPayment(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentResponseDTO> updatePayment(
    @PathVariable Integer id,
    @Valid @RequestBody PaymentRequestDTO request
  ) {
    return paymentService
      .updatePayment(id, request)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
    @PathVariable Integer id,
    @Valid @RequestBody PaymentStatusUpdateRequestDTO request
  ) {
    return paymentService
      .updatePaymentStatus(id, request.getStatus())
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
    paymentService.deletePayment(id);
    return ResponseEntity.noContent().build();
  }
}
