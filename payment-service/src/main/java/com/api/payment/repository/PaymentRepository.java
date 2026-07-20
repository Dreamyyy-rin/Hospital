package com.api.payment.repository;

import com.api.payment.model.PaymentModel;
import com.api.payment.model.PaymentModel.PaymentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, Integer> {

    List<PaymentModel> findByStatus(PaymentStatus status);
}
