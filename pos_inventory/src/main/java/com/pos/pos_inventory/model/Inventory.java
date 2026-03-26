package com.pos.pos_inventory.model;

import com.pos.pos_inventory.model.enums.LocationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"productId", "locationType", "locationId"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType locationType; // BRANCH / WAREHOUSE

    @Column(nullable = false)
    private Integer branchId; // branchId or warehouseId

    @Column(nullable = false)
    private Integer availableQuantity = 0;

    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    private Integer displayQuantity = 0; // used only for BRANCH

    private Integer reorderLevel = 0;

    private LocalDateTime createdAt;


    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

}