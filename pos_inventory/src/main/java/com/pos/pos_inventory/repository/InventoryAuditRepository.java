package com.pos.pos_inventory.repository;

import com.pos.pos_inventory.model.InventoryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryAuditRepository extends JpaRepository<InventoryAuditLog,Integer> {
}
