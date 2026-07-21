package com.api.payment.dto;

import com.api.payment.model.PaymentModel.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusUpdateRequestDTO {

  @NotNull(message = "Status is required")
  private PaymentStatus status;
}
