package com.pos.pos_inventory.repository;

import com.pos.pos_inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository <Inventory, Integer> {

    boolean existsByProductIdAndBranchId(Integer productId, Integer branchId);
}
