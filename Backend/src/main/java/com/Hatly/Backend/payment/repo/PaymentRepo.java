package com.Hatly.Backend.payment.repo;

import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
    Optional<Payment> findByProviderReferenceId(String providerReferenceId);
}
