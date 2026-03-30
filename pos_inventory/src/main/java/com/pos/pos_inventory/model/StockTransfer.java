package com.pos.pos_inventory.model;

import com.pos.pos_inventory.model.enums.BranchType;
import com.pos.pos_inventory.model.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer productId;

    @Enumerated(EnumType.STRING)
    private BranchType fromLocationType;

    private Integer fromLocationId;

    @Enumerated(EnumType.STRING)
    private BranchType toLocationType;

    private Integer toLocationId;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private TransferStatus status = TransferStatus.PENDING;

    private Integer requestedBy; // userId

    private Integer approvedBy; // warehouse manager

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}