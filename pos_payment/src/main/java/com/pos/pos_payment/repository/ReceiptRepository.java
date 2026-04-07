package com.pos.pos_payment.repository;

import com.pos.pos_payment.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    Optional<Receipt> findByPaymentId(Integer paymentId);
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
}
