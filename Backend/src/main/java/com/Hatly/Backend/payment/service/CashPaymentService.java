package com.Hatly.Backend.payment.service;

import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.payment.enums.Currency;
import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.payment.model.Payment;
import com.Hatly.Backend.payment.model.PaymentProvider;
import com.Hatly.Backend.payment.repo.PaymentProviderRepo;
import com.Hatly.Backend.payment.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CashPaymentService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private PaymentProviderRepo paymentProviderRepo;

    @Transactional
    public void processCashPayment(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order id not found"));


        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatus.PREPARING);
        orderRepo.save(order);


        PaymentProvider cashProvider = paymentProviderRepo.findByProviderName(PaymentProviderName.CASH)
                .orElseThrow(() -> new RuntimeException("Payment Provider CASH not found in DB"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(cashProvider);
        payment.setAmount(order.getTotal());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCurrency(Currency.EGP);
        payment.setRefunded(false);


        payment.setProviderReferenceId("CASH-" + orderId);

        paymentRepo.save(payment);

        System.out.println("Cash order placed successfully for order ID: " + orderId);
    }
}