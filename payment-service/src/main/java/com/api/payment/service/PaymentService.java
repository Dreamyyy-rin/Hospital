package com.api.payment.service;

import com.api.payment.dto.PaymentRequestDTO;
import com.api.payment.dto.PaymentResponseDTO;
import com.api.payment.model.PaymentModel;
import com.api.payment.model.PaymentModel.PaymentStatus;
import com.api.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  public List<PaymentResponseDTO> getAllPayments() {
    return paymentRepository
      .findAll()
      .stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  public Optional<PaymentResponseDTO> getPaymentById(Integer id) {
    return paymentRepository.findById(id).map(this::toDTO);
  }

  public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
    PaymentModel payment = new PaymentModel();
    payment.setAppointmentId(request.getAppointmentId());
    payment.setTotalAmount(request.getTotalAmount());
    payment.setAmountPaid(request.getAmountPaid());
    payment.setPaymentMethod(request.getPaymentMethod());

    if (payment.getAmountPaid().compareTo(payment.getTotalAmount()) < 0) {
      throw new IllegalArgumentException(
        "Amount paid (" +
          payment.getAmountPaid() +
          ") is less than total amount (" +
          payment.getTotalAmount() +
          ")"
      );
    }

    payment.setStatus(PaymentStatus.PENDING);
    payment.setCreatedAt(LocalDateTime.now());
    payment.setUpdatedAt(LocalDateTime.now());

    BigDecimal change = payment
      .getAmountPaid()
      .subtract(payment.getTotalAmount());
    payment.setChangeAmount(
      change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO
    );

    PaymentModel saved = paymentRepository.save(payment);
    return toDTO(saved);
  }

  public Optional<PaymentResponseDTO> updatePayment(
    Integer id,
    PaymentRequestDTO request
  ) {
    return paymentRepository.findById(id).map(payment -> {
      if (request.getAmountPaid().compareTo(request.getTotalAmount()) < 0) {
        throw new IllegalArgumentException(
          "Amount paid (" +
            request.getAmountPaid() +
            ") is less than total amount (" +
            request.getTotalAmount() +
            ")"
        );
      }

      payment.setTotalAmount(request.getTotalAmount());
      payment.setAmountPaid(request.getAmountPaid());
      payment.setPaymentMethod(request.getPaymentMethod());

      BigDecimal change = request
        .getAmountPaid()
        .subtract(request.getTotalAmount());
      payment.setChangeAmount(
        change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO
      );

      payment.setUpdatedAt(LocalDateTime.now());
      PaymentModel saved = paymentRepository.save(payment);
      return toDTO(saved);
    });
  }

  public Optional<PaymentResponseDTO> updatePaymentStatus(
    Integer id,
    PaymentStatus newStatus
  ) {
    return paymentRepository.findById(id).map(payment -> {
      payment.setStatus(newStatus);
      payment.setUpdatedAt(LocalDateTime.now());
      PaymentModel saved = paymentRepository.save(payment);
      return toDTO(saved);
    });
  }

  public void deletePayment(Integer id) {
    paymentRepository.deleteById(id);
  }

  public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
    return paymentRepository
      .findByStatus(status)
      .stream()
      .map(this::toDTO)
      .collect(Collectors.toList());
  }

  public PaymentResponseDTO toDTO(PaymentModel payment) {
    return PaymentResponseDTO.builder()
      .id(payment.getId())
      .appointmentId(payment.getAppointmentId())
      .totalAmount(payment.getTotalAmount())
      .amountPaid(payment.getAmountPaid())
      .changeAmount(payment.getChangeAmount())
      .paymentMethod(payment.getPaymentMethod())
      .status(payment.getStatus())
      .createdAt(payment.getCreatedAt())
      .updatedAt(payment.getUpdatedAt())
      .build();
  }
}
