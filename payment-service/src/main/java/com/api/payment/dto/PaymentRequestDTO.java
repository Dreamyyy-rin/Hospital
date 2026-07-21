package com.api.payment.dto;

import com.api.payment.model.PaymentModel.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

  private Integer appointmentId;

  @NotNull(message = "Total amount is required")
  @Positive(message = "Total amount must be positive")
  private BigDecimal totalAmount;

  @NotNull(message = "Amount paid is required")
  @Positive(message = "Amount paid must be positive")
  private BigDecimal amountPaid;

  @NotNull(message = "Payment method is required")
  private PaymentMethod paymentMethod;
}
