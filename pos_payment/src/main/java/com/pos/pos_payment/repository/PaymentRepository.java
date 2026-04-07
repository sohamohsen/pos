package com.pos.pos_payment.repository;

import com.pos.pos_payment.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Page<Payment> findByBranchId(Integer branchId, Pageable pageable);

    Page<Payment> findByCashierId(Integer cashierId, Pageable pageable);

    @Query("""
            SELECT p FROM Payment p
            WHERE (:branchId IS NULL OR p.branchId = :branchId)
              AND (:cashierId IS NULL OR p.cashierId = :cashierId)
            """)
    Page<Payment> findPayments(
            @Param("branchId") Integer branchId,
            @Param("cashierId") Integer cashierId,
            Pageable pageable
    );
}
