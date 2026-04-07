package com.pos.pos_inventory.repository;

import com.pos.pos_inventory.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByInventoryIdOrderByCreatedAtDesc(Long inventoryId);
}
