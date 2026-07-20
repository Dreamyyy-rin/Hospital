package com.api.payment.controller;

import com.api.payment.model.PaymentModel;
import com.api.payment.service.PaymentService;
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
  public List<PaymentModel> getAllPayments() {
    return paymentService.getAllPayments();
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentModel> getPaymentById(@PathVariable Integer id) {
    return paymentService
      .getPaymentById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public PaymentModel createPayment(@RequestBody PaymentModel payment) {
    return paymentService.createPayment(payment);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentModel> updatePayment(
    @PathVariable Integer id,
    @RequestBody PaymentModel payment
  ) {
    try {
      return ResponseEntity.ok(paymentService.updatePayment(id, payment));
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<PaymentModel> updatePaymentStatus(
    @PathVariable Integer id,
    @RequestBody PaymentModel payment
  ) {
    try {
      return ResponseEntity.ok(
        paymentService.updatePaymentStatus(id, payment.getStatus())
      );
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
    paymentService.deletePayment(id);
    return ResponseEntity.noContent().build();
  }
}
