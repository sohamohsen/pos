package com.pos.pos_inventory.model;

import com.pos.pos_inventory.model.enums.MovementReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long inventoryId;

    @Column(nullable = false)
    private Integer delta;              // positive = stock in, negative = stock out

    @Column(nullable = false)
    private Integer quantityAfter;      // snapshot after the change

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementReason reason;      // SALE, ADJUSTMENT, TRANSFER_IN, TRANSFER_OUT, RETURN, INITIAL

    private String referenceId;         // orderId, transferId, etc.

    private Integer performedBy;        // userId who triggered the change

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}