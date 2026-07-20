package com.api.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "payment")
@Data
public class PaymentModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal totalAmount;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal amountPaid;

  @Column(precision = 15, scale = 2)
  private BigDecimal changeAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  public enum PaymentMethod {
    CASH,
    DEBIT_CARD,
    CREDIT_CARD,
    TRANSFER,
    INSURANCE,
  }

  public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED,
  }
}
