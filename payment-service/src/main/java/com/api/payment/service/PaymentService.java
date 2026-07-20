package com.api.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.payment.model.PaymentModel;
import com.api.payment.model.PaymentModel.PaymentStatus;
import com.api.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentModel> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<PaymentModel> getPaymentById(Integer id) {
        return paymentRepository.findById(id);
    }

    public PaymentModel createPayment(PaymentModel payment) {
        if (payment.getAmountPaid().compareTo(payment.getTotalAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Amount paid (" + payment.getAmountPaid() +
                    ") is less than total amount (" + payment.getTotalAmount() + ")");
        }
        
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        BigDecimal change = payment.getAmountPaid().subtract(payment.getTotalAmount());
        payment.setChangeAmount(change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO);
        
        return paymentRepository.save(payment);
    }

    public PaymentModel updatePayment(Integer id, PaymentModel updatedPayment) {
        return paymentRepository.findById(id).map(payment -> {
            if (updatedPayment.getAmountPaid().compareTo(updatedPayment.getTotalAmount()) < 0) {
                throw new IllegalArgumentException(
                        "Amount paid (" + updatedPayment.getAmountPaid() +
                        ") is less than total amount (" + updatedPayment.getTotalAmount() + ")");
            }
            
            payment.setTotalAmount(updatedPayment.getTotalAmount());
            payment.setAmountPaid(updatedPayment.getAmountPaid());
            payment.setPaymentMethod(updatedPayment.getPaymentMethod());
            
            BigDecimal change = updatedPayment.getAmountPaid().subtract(updatedPayment.getTotalAmount());
            payment.setChangeAmount(change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO);
            
            payment.setUpdatedAt(LocalDateTime.now());
            return paymentRepository.save(payment);
        }).orElseThrow(() -> new RuntimeException("Payment not found with id " + id));
    }

    public PaymentModel updatePaymentStatus(Integer id, PaymentStatus newStatus) {
        return paymentRepository.findById(id).map(payment -> {
            payment.setStatus(newStatus);
            payment.setUpdatedAt(LocalDateTime.now());
            return paymentRepository.save(payment);
        }).orElseThrow(() -> new RuntimeException("Payment not found with id " + id));
    }

    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }

    public List<PaymentModel> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
}
