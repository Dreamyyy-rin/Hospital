package com.api.payment.dto;

import com.api.payment.model.PaymentModel.PaymentMethod;
import com.api.payment.model.PaymentModel.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

  private Integer id;
  private Integer appointmentId;
  private BigDecimal totalAmount;
  private BigDecimal amountPaid;
  private BigDecimal changeAmount;
  private PaymentMethod paymentMethod;
  private PaymentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
