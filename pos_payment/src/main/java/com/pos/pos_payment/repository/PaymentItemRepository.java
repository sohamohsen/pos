package com.pos.pos_payment.repository;

import com.pos.pos_payment.model.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentItemRepository extends JpaRepository<PaymentItem, Integer> {
    List<PaymentItem> findByPaymentId(Integer paymentId);
}
