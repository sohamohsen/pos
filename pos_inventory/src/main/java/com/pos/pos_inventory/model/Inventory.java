package com.pos.pos_inventory.model;

import com.pos.pos_inventory.model.enums.BranchType;
import com.pos.pos_inventory.model.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "branch_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "branch_id", nullable = false)
    private Integer branchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private BranchType locationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InventoryStatus status;

    @Column(name = "available_quantity", nullable = false)
    @Builder.Default
    private Integer availableQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    @Column(name = "display_quantity")
    @Builder.Default
    private Integer displayQuantity = 0;

    @Column(name = "damaged_quantity")
    @Builder.Default
    private Integer damagedQuantity = 0;

    @Column(name = "reorder_level")
    @Builder.Default
    private Integer reorderLevel = 0;

    @Column(name = "reorder_quantity")
    @Builder.Default
    private Integer reorderQuantity = 0;

    @Column(name = "notification_sent")
    @Builder.Default
    private Boolean notificationSent = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}