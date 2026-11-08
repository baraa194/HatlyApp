package com.Hatly.Backend.payment.repo;

import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.model.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentProviderRepo extends JpaRepository<PaymentProvider, Long>
{
    Optional<PaymentProvider> findByProviderName(PaymentProviderName name);
    Optional<List<PaymentProvider>> findByIsEnabledTrueOrderByPriorityAsc();
}
