package com.pos.pos_inventory.model;

import com.pos.pos_inventory.model.enums.AuditAction;
import com.pos.pos_inventory.model.enums.AuditField;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer inventoryId;
    private Integer productId;

    @Enumerated(EnumType.STRING)
    private AuditAction actionType;

    @Enumerated(EnumType.STRING)
    private AuditField fieldName;

    private String oldValue;
    private String newValue;

    private Integer changedBy;

    private LocalDateTime changedAt;

    @PrePersist
    public void onCreate() {
        changedAt = LocalDateTime.now();
    }
}